package com.caring.caringbackend.api.internal.chat.controller;

import com.caring.caringbackend.api.internal.chat.dto.request.ChatMessageSendRequest;
import com.caring.caringbackend.api.internal.chat.dto.request.ChatStartRequest;
import com.caring.caringbackend.api.internal.chat.dto.response.ChatMessageListResponse;
import com.caring.caringbackend.api.internal.chat.dto.response.ChatMessageResponse;
import com.caring.caringbackend.api.internal.chat.dto.response.ChatRoomInfoResponse;
import com.caring.caringbackend.api.internal.chat.dto.response.ChatStartResponse;
import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import com.caring.caringbackend.domain.chat.entity.SenderType;
import com.caring.caringbackend.domain.chat.service.ChatService;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
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
@RequestMapping("/api/v1/members/chat/me")
@RequiredArgsConstructor
@Tag(name = "💬 Member Chat", description = "회원 상담 채팅 API")
@SecurityRequirement(name = "bearerAuth")
public class MemberChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    @PostMapping("/start")
    @Operation(summary = "상담 시작", description = "회원이 상담을 시작합니다. ConsultRequest와 ChatRoom이 동시에 생성됩니다.")
    public ResponseEntity<ApiResponse<ChatStartResponse>> startChat(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ChatStartRequest request) {

        ChatRoom chatRoom = chatService.startChat(
                memberDetails.getId(),
                request.getInstitutionId(),
                request.getCounselId()
        );

        ChatStartResponse response = ChatStartResponse.from(chatRoom);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("상담 시작 성공", response));
    }

    @PostMapping("/rooms/{chatRoomId}/messages")
    @Operation(summary = "메시지 전송", description = "회원이 채팅 메시지를 전송합니다.")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageSendRequest request) {

        ChatMessage message = chatService.sendMessage(
                chatRoomId,
                SenderType.MEMBER,
                memberDetails.getId(),
                request.getContent()
        );

        Member member = memberRepository.findByIdAndDeletedFalse(memberDetails.getId())
                .orElseThrow(() -> new MemberNotFoundException(memberDetails.getId()));

        ChatMessageResponse response = ChatMessageResponse.from(message, member.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("메시지 전송 성공", response));
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "회원이 채팅방의 메시지 목록을 조회합니다. (페이징)")
    public ResponseEntity<ApiResponse<ChatMessageListResponse>> getMessages(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {

        Page<ChatMessage> messagePage = chatService.getMessages(
                chatRoomId,
                memberDetails.getId(),
                SenderType.MEMBER,
                pageable
        );

        List<ChatMessageResponse> messageResponses = messagePage.getContent().stream()
                .map(message -> ChatMessageResponse.from(message, getSenderName(message)))
                .collect(Collectors.toList());

        ChatMessageListResponse response = ChatMessageListResponse.of(messageResponses, messagePage);
        return ResponseEntity.ok(ApiResponse.success("메시지 목록 조회 성공", response));
    }

    @GetMapping("/rooms/{chatRoomId}/messages/poll")
    @Operation(summary = "롱 폴링", description = "회원이 신규 메시지를 대기합니다. 타임아웃: 30초")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> pollMessages(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @RequestParam Long lastMessageId) {

        List<ChatMessage> newMessages = chatService.pollMessages(
                chatRoomId,
                lastMessageId,
                memberDetails.getId(),
                SenderType.MEMBER
        );

        List<ChatMessageResponse> messageResponses = newMessages.stream()
                .map(message -> ChatMessageResponse.from(message, getSenderName(message)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("신규 메시지 조회 성공", messageResponses));
    }

    @DeleteMapping("/rooms/{chatRoomId}/messages/{messageId}")
    @Operation(summary = "메시지 삭제", description = "회원이 본인이 보낸 메시지를 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {

        chatService.deleteMessage(chatRoomId, messageId, memberDetails.getId(), SenderType.MEMBER);
        return ResponseEntity.ok(ApiResponse.success("메시지 삭제 성공", null));
    }

    @GetMapping("/rooms/{chatRoomId}")
    @Operation(summary = "채팅방 정보 조회", description = "회원이 채팅방 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomInfoResponse>> getChatRoomInfo(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId) {

        ChatRoom chatRoom = chatService.getChatRoomInfo(chatRoomId, memberDetails.getId(), SenderType.MEMBER);
        ChatRoomInfoResponse response = ChatRoomInfoResponse.from(chatRoom);
        return ResponseEntity.ok(ApiResponse.success("채팅방 정보 조회 성공", response));
    }

    @PostMapping("/rooms/{chatRoomId}/close")
    @Operation(summary = "상담 종료", description = "회원이 상담을 종료합니다.")
    public ResponseEntity<ApiResponse<Void>> closeChat(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId) {

        chatService.closeChat(chatRoomId, memberDetails.getId(), SenderType.MEMBER);
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

