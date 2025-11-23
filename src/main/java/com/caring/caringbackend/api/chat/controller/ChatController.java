package com.caring.caringbackend.api.chat.controller;

import com.caring.caringbackend.api.chat.dto.request.ChatMessageSendRequest;
import com.caring.caringbackend.api.chat.dto.request.ChatStartRequest;
import com.caring.caringbackend.api.chat.dto.response.*;
import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import com.caring.caringbackend.domain.chat.entity.SenderType;
import com.caring.caringbackend.domain.chat.service.ChatService;
import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequestStatus;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 💬 채팅 관리 Controller
 * <p>
 * 상담 채팅 기능을 제공하는 REST API 엔드포인트입니다.
 * - 회원용 API: 상담 시작, 메시지 전송/조회, 상담 종료
 * - 기관 관리자용 API: 메시지 전송/조회, 상담 종료
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "💬 Chat", description = "채팅 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    /**
     * 상담 시작 (회원 전용)
     * - ConsultRequest + ChatRoom 동시 생성
     */
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

    /**
     * 메시지 전송 (회원용)
     */
    @PostMapping("/rooms/{chatRoomId}/messages/member")
    @Operation(summary = "메시지 전송 (회원)", description = "회원이 채팅 메시지를 전송합니다.")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessageAsMember(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageSendRequest request) {

        ChatMessage message = chatService.sendMessage(
                chatRoomId,
                SenderType.MEMBER,
                memberDetails.getId(),
                request.getContent()
        );

        // 발신자 이름 조회 (회원 이름)
        Member member = memberRepository.findByIdAndDeletedFalse(memberDetails.getId())
                .orElseThrow(() -> new MemberNotFoundException(memberDetails.getId()));

        ChatMessageResponse response = ChatMessageResponse.from(message, member.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("메시지 전송 성공", response));
    }

    /**
     * 메시지 전송 (기관 관리자용)
     */
    @PostMapping("/rooms/{chatRoomId}/messages/admin")
    @Operation(summary = "메시지 전송 (기관 관리자)", description = "기관 관리자가 채팅 메시지를 전송합니다. 메시지는 기관명으로 표시됩니다.")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessageAsAdmin(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageSendRequest request) {

        ChatMessage message = chatService.sendMessage(
                chatRoomId,
                SenderType.INSTITUTION_ADMIN,
                adminDetails.getId(),
                request.getContent()
        );

        // 발신자 이름 조회 (기관명)
        InstitutionAdmin admin = institutionAdminRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        String institutionName = admin.getInstitution() != null ?
                admin.getInstitution().getName() : "알 수 없음";

        ChatMessageResponse response = ChatMessageResponse.from(message, institutionName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("메시지 전송 성공", response));
    }

    /**
     * 메시지 목록 조회 (회원용)
     */
    @GetMapping("/rooms/{chatRoomId}/messages/member")
    @Operation(summary = "메시지 목록 조회 (회원)", description = "회원이 채팅방의 메시지 목록을 조회합니다. (페이징)")
    public ResponseEntity<ApiResponse<ChatMessageListResponse>> getMessagesAsMember(
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
                .map(message -> {
                    String senderName = getSenderName(message);
                    return ChatMessageResponse.from(message, senderName);
                })
                .collect(Collectors.toList());

        ChatMessageListResponse response = ChatMessageListResponse.of(messageResponses, messagePage);
        return ResponseEntity.ok(ApiResponse.success("메시지 목록 조회 성공", response));
    }

    /**
     * 메시지 목록 조회 (기관 관리자용)
     */
    @GetMapping("/rooms/{chatRoomId}/messages/admin")
    @Operation(summary = "메시지 목록 조회 (기관 관리자)", description = "기관 관리자가 채팅방의 메시지 목록을 조회합니다. (페이징)")
    public ResponseEntity<ApiResponse<ChatMessageListResponse>> getMessagesAsAdmin(
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
                .map(message -> {
                    String senderName = getSenderName(message);
                    return ChatMessageResponse.from(message, senderName);
                })
                .collect(Collectors.toList());

        ChatMessageListResponse response = ChatMessageListResponse.of(messageResponses, messagePage);
        return ResponseEntity.ok(ApiResponse.success("메시지 목록 조회 성공", response));
    }

    /**
     * 롱 폴링 - 신규 메시지 대기 (회원용)
     */
    @GetMapping("/rooms/{chatRoomId}/messages/poll/member")
    @Operation(summary = "롱 폴링 (회원)", description = "회원이 신규 메시지를 대기합니다. 타임아웃: 30초")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> pollMessagesAsMember(
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
                .map(message -> {
                    String senderName = getSenderName(message);
                    return ChatMessageResponse.from(message, senderName);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("신규 메시지 조회 성공", messageResponses));
    }

    /**
     * 롱 폴링 - 신규 메시지 대기 (기관 관리자용)
     */
    @GetMapping("/rooms/{chatRoomId}/messages/poll/admin")
    @Operation(summary = "롱 폴링 (기관 관리자)", description = "기관 관리자가 신규 메시지를 대기합니다. 타임아웃: 30초")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> pollMessagesAsAdmin(
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
                .map(message -> {
                    String senderName = getSenderName(message);
                    return ChatMessageResponse.from(message, senderName);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("신규 메시지 조회 성공", messageResponses));
    }

    /**
     * 메시지 삭제 (회원용)
     */
    @DeleteMapping("/rooms/{chatRoomId}/messages/{messageId}/member")
    @Operation(summary = "메시지 삭제 (회원)", description = "회원이 본인이 보낸 메시지를 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessageAsMember(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {

        chatService.deleteMessage(chatRoomId, messageId, memberDetails.getId(), SenderType.MEMBER);
        return ResponseEntity.ok(ApiResponse.success("메시지 삭제 성공", null));
    }

    /**
     * 메시지 삭제 (기관 관리자용)
     */
    @DeleteMapping("/rooms/{chatRoomId}/messages/{messageId}/admin")
    @Operation(summary = "메시지 삭제 (기관 관리자)", description = "기관 관리자가 본인이 보낸 메시지를 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessageAsAdmin(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {

        chatService.deleteMessage(chatRoomId, messageId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        return ResponseEntity.ok(ApiResponse.success("메시지 삭제 성공", null));
    }

    /**
     * 채팅방 정보 조회 (회원용)
     */
    @GetMapping("/rooms/{chatRoomId}/member")
    @Operation(summary = "채팅방 정보 조회 (회원)", description = "회원이 채팅방 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomInfoResponse>> getChatRoomInfoAsMember(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId) {

        ChatRoom chatRoom = chatService.getChatRoomInfo(chatRoomId, memberDetails.getId(), SenderType.MEMBER);
        ChatRoomInfoResponse response = ChatRoomInfoResponse.from(chatRoom);
        return ResponseEntity.ok(ApiResponse.success("채팅방 정보 조회 성공", response));
    }

    /**
     * 채팅방 정보 조회 (기관 관리자용)
     */
    @GetMapping("/rooms/{chatRoomId}/admin")
    @Operation(summary = "채팅방 정보 조회 (기관 관리자)", description = "기관 관리자가 채팅방 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomInfoResponse>> getChatRoomInfoAsAdmin(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId) {

        ChatRoom chatRoom = chatService.getChatRoomInfo(chatRoomId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        ChatRoomInfoResponse response = ChatRoomInfoResponse.from(chatRoom);
        return ResponseEntity.ok(ApiResponse.success("채팅방 정보 조회 성공", response));
    }

    /**
     * 상담 종료 (회원용)
     */
    @PostMapping("/rooms/{chatRoomId}/close/member")
    @Operation(summary = "상담 종료 (회원)", description = "회원이 상담을 종료합니다.")
    public ResponseEntity<ApiResponse<Void>> closeChatAsMember(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long chatRoomId) {

        chatService.closeChat(chatRoomId, memberDetails.getId(), SenderType.MEMBER);
        return ResponseEntity.ok(ApiResponse.success("상담 종료 성공", null));
    }

    /**
     * 상담 종료 (기관 관리자용)
     */
    @PostMapping("/rooms/{chatRoomId}/close/admin")
    @Operation(summary = "상담 종료 (기관 관리자)", description = "기관 관리자가 상담을 종료합니다.")
    public ResponseEntity<ApiResponse<Void>> closeChatAsAdmin(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long chatRoomId) {

        chatService.closeChat(chatRoomId, adminDetails.getId(), SenderType.INSTITUTION_ADMIN);
        return ResponseEntity.ok(ApiResponse.success("상담 종료 성공", null));
    }

    /**
     * 기관의 상담 요청 목록 조회
     */
    @GetMapping("/institutions/{institutionId}/consult-requests")
    @Operation(summary = "기관 상담 요청 목록 조회", description = "기관 관리자가 소속 기관의 상담 요청 목록을 조회합니다. (페이징, 상태 필터링 지원)")
    public ResponseEntity<ApiResponse<ConsultRequestListResponse>> getInstitutionConsultRequests(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @Parameter(description = "상태 필터 (ACTIVE: 진행 중, CLOSED: 종료됨, null: 전체)")
            @RequestParam(required = false) ConsultRequestStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ConsultRequestListResponse response = chatService.getInstitutionConsultRequests(
                institutionId, adminDetails.getId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success("상담 요청 목록 조회 성공", response));
    }

    /**
     * 발신자 이름 조회 헬퍼 메서드
     * - 회원: 회원 이름
     * - 기관 관리자: 기관명
     */
    private String getSenderName(ChatMessage message) {
        if (message.getSenderType() == SenderType.MEMBER) {
            return memberRepository.findByIdAndDeletedFalse(message.getSenderId())
                    .map(Member::getName)
                    .orElse("알 수 없음");
        } else {
            return institutionAdminRepository.findById(message.getSenderId())
                    .map(admin -> admin.getInstitution() != null ?
                            admin.getInstitution().getName() : "알 수 없음")
                    .orElse("알 수 없음");
        }
    }
}

