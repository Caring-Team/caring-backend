package com.caring.caringbackend.api.chat.controller;

import com.caring.caringbackend.api.chat.dto.request.ChatMessageSendRequest;
import com.caring.caringbackend.api.chat.dto.response.ChatMessageListResponse;
import com.caring.caringbackend.api.chat.dto.response.ChatMessageResponse;
import com.caring.caringbackend.api.chat.dto.response.ChatRoomInfoResponse;
import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import com.caring.caringbackend.domain.chat.entity.SenderType;
import com.caring.caringbackend.domain.chat.service.ChatService;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/institution/chat")
@RequiredArgsConstructor
@Tag(name = "🏥 Institution Chat", description = "기관 상담 채팅 API")
@SecurityRequirement(name = "bearerAuth")
public class InstitutionChatController {

    private final ChatService chatService;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/rooms/{chatRoomId}/messages")
    @Operation(summary = "메시지 전송", description = "기관 관리자가 채팅 메시지를 전송합니다. 메시지는 기관명으로 표시됩니다.")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageSendRequest request) {

        ChatMessage message = chatService.sendMessage(
                chatRoomId,
                SenderType.INSTITUTION_ADMIN,
                adminDetails.getId(),
                request.getContent()
        );

        InstitutionAdmin admin = institutionAdminRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        String institutionName = admin.getInstitution() != null ?
                admin.getInstitution().getName() : "알 수 없음";

        ChatMessageResponse response = ChatMessageResponse.from(message, institutionName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("메시지 전송 성공", response));
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "기관 관리자가 채팅방의 메시지 목록을 조회합니다. (페이징)")
    public ResponseEntity<ApiResponse<ChatMessageListResponse>> getMessages(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {

        Page<ChatMessage> messagePage = chatService.getMessages(
                chatRoomId,
                adminDetails.getId(),
                SenderType.INSTITUTION_ADMIN,
                pageable
        );

        List<ChatMessageResponse> messageResponses = messagePage.getContent().stream()
                .map(message -> ChatMessageResponse.from(message, getSenderName(message)))
                .collect(Collectors.toList());

        ChatMessageListResponse response = ChatMessageListResponse.of(messageResponses, messagePage);
        return ResponseEntity.ok(ApiResponse.success("메시지 목록 조회 성공", response));
    }

    @GetMapping("/rooms/{chatRoomId}/messages/poll")
    @Operation(summary = "롱 폴링", description = "기관 관리자가 신규 메시지를 대기합니다. 타임아웃: 30초")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> pollMessages(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @RequestParam Long lastMessageId) {

        List<ChatMessage> newMessages = chatService.pollMessages(
                chatRoomId,
                lastMessageId,
                adminDetails.getId(),
                SenderType.INSTITUTION_ADMIN
        );

        List<ChatMessageResponse> messageResponses = newMessages.stream()
                .map(message -> ChatMessageResponse.from(message, getSenderName(message)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("신규 메시지 조회 성공", messageResponses));
    }

    @DeleteMapping("/rooms/{chatRoomId}/messages/{messageId}")
    @Operation(summary = "메시지 삭제", description = "기관 관리자가 본인이 보낸 메시지를 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {

        chatService.deleteMessage(chatRoomId, messageId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        return ResponseEntity.ok(ApiResponse.success("메시지 삭제 성공", null));
    }

    @GetMapping("/rooms/{chatRoomId}")
    @Operation(summary = "채팅방 정보 조회", description = "기관 관리자가 채팅방 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomInfoResponse>> getChatRoomInfo(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId) {

        ChatRoom chatRoom = chatService.getChatRoomInfo(chatRoomId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        ChatRoomInfoResponse response = ChatRoomInfoResponse.from(chatRoom);
        return ResponseEntity.ok(ApiResponse.success("채팅방 정보 조회 성공", response));
    }

    @PostMapping("/rooms/{chatRoomId}/close")
    @Operation(summary = "상담 종료", description = "기관 관리자가 상담을 종료합니다.")
    public ResponseEntity<ApiResponse<Void>> closeChat(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId) {

        chatService.closeChat(chatRoomId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        return ResponseEntity.ok(ApiResponse.success("상담 종료 성공", null));
    }

    private String getSenderName(ChatMessage message) {
        if (message.getSenderType() == SenderType.MEMBER) {
            return memberRepository.findByIdAndDeletedFalse(message.getSenderId())
                    .map(Member::getName)
                    .orElse("알 수 없음");
        }

        return institutionAdminRepository.findById(message.getSenderId())
                .map(admin -> admin.getInstitution() != null ?
                        admin.getInstitution().getName() : "알 수 없음")
                .orElse("알 수 없음");
    }
}

