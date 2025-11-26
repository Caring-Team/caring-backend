package com.caring.caringbackend.domain.chat.service;

import com.caring.caringbackend.api.internal.chat.dto.response.ConsultRequestListResponse;
import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import com.caring.caringbackend.domain.chat.entity.SenderType;
import com.caring.caringbackend.domain.chat.repository.ChatMessageRepository;
import com.caring.caringbackend.domain.chat.repository.ChatRoomRepository;
import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequest;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.ConsultRequestStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.repository.ConsultRequestRepository;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselRepository;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅 비즈니스 로직을 처리하는 서비스
 * - 상담 시작 (ConsultRequest + ChatRoom 동시 생성)
 * - 메시지 전송/조회/삭제
 * - 롱 폴링 (신규 메시지 대기)
 * - 상담 종료
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ConsultRequestRepository consultRequestRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final InstitutionRepository institutionRepository;
    private final InstitutionCounselRepository institutionCounselRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    /**
     * 상담 시작 (ConsultRequest + ChatRoom 동시 생성)
     * - 회원이 '상담 시작' 버튼 클릭 시 호출
     * - 같은 회원 + 같은 상담 서비스에 대해 ACTIVE 상태는 하나만 존재
     *
     * @param memberId 회원 ID
     * @param institutionId 기관 ID
     * @param counselId 상담 서비스 ID
     * @return 생성된 ChatRoom
     */
    @Transactional
    public ChatRoom startChat(Long memberId, Long institutionId, Long counselId) {
        // 1. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 기관 존재 확인
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND));

        // 3. 상담 서비스 존재 확인 및 기관 일치 확인
        InstitutionCounsel counsel = institutionCounselRepository.findById(counselId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUNSEL_NOT_FOUND));

        if (!counsel.getInstitution().getId().equals(institutionId)) {
            throw new BusinessException(ErrorCode.COUNSEL_NOT_FOUND);
        }

        // 4. ACTIVE 상태 중복 확인 (같은 회원 + 같은 상담 서비스)
        boolean activeExists = consultRequestRepository.existsActiveByMemberAndCounsel(memberId, counselId);
        if (activeExists) {
            throw new BusinessException(ErrorCode.ACTIVE_CONSULT_REQUEST_EXISTS);
        }

        // 5. ConsultRequest 생성 (message는 null)
        ConsultRequest consultRequest = ConsultRequest.create(member, institution, counsel, null);
        ConsultRequest savedConsultRequest = consultRequestRepository.save(consultRequest);

        // 6. ChatRoom 생성
        ChatRoom chatRoom = ChatRoom.create(savedConsultRequest);
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 7. Lazy Loading 방지: ConsultRequest와 Institution 미리 로드
        // Controller에서 ChatStartResponse.from() 호출 시 Lazy Loading 발생 방지
        savedChatRoom.getConsultRequest().getId();
        savedChatRoom.getConsultRequest().getInstitution().getId();
        savedChatRoom.getConsultRequest().getInstitution().getName();

        log.info("상담 시작 완료: consultRequestId={}, chatRoomId={}, memberId={}, institutionId={}, counselId={}",
                savedConsultRequest.getId(), savedChatRoom.getId(), memberId, institutionId, counselId);

        return savedChatRoom;
    }

    /**
     * 메시지 전송
     * - 회원 또는 기관 관리자가 메시지 전송
     * - 채팅방 활성화 상태 확인
     * - 마지막 메시지 정보 업데이트
     *
     * @param chatRoomId 채팅방 ID
     * @param senderType 발신자 유형 (MEMBER, INSTITUTION_ADMIN)
     * @param senderId 발신자 ID
     * @param content 메시지 내용
     * @return 전송된 메시지
     */
    @Transactional
    public ChatMessage sendMessage(Long chatRoomId, SenderType senderType, Long senderId, String content) {
        // 1. 채팅방 조회 (ConsultRequest 포함, Lazy Loading 방지)
        ChatRoom chatRoom = chatRoomRepository.findByIdWithConsultRequest(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 2. 채팅방 활성화 상태 확인
        if (!chatRoom.getIsActive()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ALREADY_CLOSED);
        }

        // 3. ConsultRequest 활성화 상태 확인
        if (!chatRoom.getConsultRequest().isActive()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ALREADY_CLOSED);
        }

        // 4. 발신자 권한 검증
        validateSenderPermission(chatRoom, senderType, senderId);

        // 5. 메시지 생성
        ChatMessage message = ChatMessage.create(chatRoom, senderType, senderId, content);
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 6. 채팅방 마지막 메시지 정보 업데이트
        chatRoom.updateLastMessage(content, savedMessage.getCreatedAt());

        log.info("메시지 전송 완료: messageId={}, chatRoomId={}, senderType={}, senderId={}",
                savedMessage.getId(), chatRoomId, senderType, senderId);

        return savedMessage;
    }

    /**
     * 메시지 목록 조회 (페이징)
     * - 삭제되지 않은 메시지만 조회
     * - 최신순 정렬
     *
     * @param chatRoomId 채팅방 ID
     * @param userId 요청자 ID (회원 또는 기관 관리자)
     * @param userType 요청자 유형 (MEMBER, INSTITUTION_ADMIN)
     * @param pageable 페이징 정보
     * @return 메시지 목록
     */
    public Page<ChatMessage> getMessages(Long chatRoomId, Long userId, SenderType userType, Pageable pageable) {
        // 1. 채팅방 조회 및 권한 검증
        getChatRoomWithPermission(chatRoomId, userId, userType);

        // 2. 메시지 목록 조회 (Soft Delete 제외)
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndNotDeleted(chatRoomId, pageable);

        log.debug("메시지 목록 조회: chatRoomId={}, userId={}, userType={}, totalElements={}",
                chatRoomId, userId, userType, messages.getTotalElements());

        return messages;
    }

    /**
     * 롱 폴링 - 신규 메시지 대기
     * - lastMessageId 이후의 신규 메시지 조회
     * - 타임아웃: 30초
     * - 폴링 간격: 0.5초
     *
     * @param chatRoomId 채팅방 ID
     * @param lastMessageId 마지막 메시지 ID
     * @param userId 요청자 ID
     * @param userType 요청자 유형
     * @return 신규 메시지 목록 (없으면 빈 리스트)
     */
    public List<ChatMessage> pollMessages(Long chatRoomId, Long lastMessageId, Long userId, SenderType userType) {
        // 1. 채팅방 조회 및 권한 검증
        getChatRoomWithPermission(chatRoomId, userId, userType);

        // 2. 롱 폴링 로직
        long startTime = System.currentTimeMillis();
        long timeout = 30000; // 30초
        long pollingInterval = 500; // 0.5초

        while (System.currentTimeMillis() - startTime < timeout) {
            // 신규 메시지 조회
            List<ChatMessage> newMessages = chatMessageRepository.findNewMessages(chatRoomId, lastMessageId);

            if (!newMessages.isEmpty()) {
                log.debug("신규 메시지 발견: chatRoomId={}, count={}", chatRoomId, newMessages.size());
                return newMessages;
            }

            // 폴링 간격만큼 대기
            try {
                Thread.sleep(pollingInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("롱 폴링 중단: chatRoomId={}", chatRoomId);
                break;
            }
        }

        log.debug("롱 폴링 타임아웃: chatRoomId={}, lastMessageId={}", chatRoomId, lastMessageId);
        return List.of(); // 타임아웃 시 빈 리스트 반환
    }

    /**
     * 메시지 삭제 (Soft Delete)
     * - 본인이 보낸 메시지만 삭제 가능
     *
     * @param chatRoomId 채팅방 ID
     * @param messageId 메시지 ID
     * @param userId 요청자 ID
     * @param userType 요청자 유형
     */
    @Transactional
    public void deleteMessage(Long chatRoomId, Long messageId, Long userId, SenderType userType) {
        // 1. 채팅방 조회 및 권한 검증
        getChatRoomWithPermission(chatRoomId, userId, userType);

        // 2. 메시지 조회
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));

        // 3. 메시지가 해당 채팅방의 것인지 확인
        if (!message.getChatRoom().getId().equals(chatRoomId)) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        // 4. 본인이 보낸 메시지인지 확인
        if (!message.getSenderType().equals(userType) || !message.getSenderId().equals(userId)) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_DELETE_DENIED);
        }

        // 5. 이미 삭제된 메시지인지 확인
        if (message.isDeleted()) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
        }

        // 6. Soft Delete 처리
        message.delete();

        log.info("메시지 삭제 완료: messageId={}, chatRoomId={}, userId={}, userType={}",
                messageId, chatRoomId, userId, userType);
    }

    /**
     * 채팅방 정보 조회
     *
     * @param chatRoomId 채팅방 ID
     * @param userId 요청자 ID
     * @param userType 요청자 유형
     * @return 채팅방 정보
     */
    public ChatRoom getChatRoomInfo(Long chatRoomId, Long userId, SenderType userType) {
        ChatRoom chatRoom = getChatRoomWithPermission(chatRoomId, userId, userType);
        
        // Lazy Loading 방지: Controller에서 ChatRoomInfoResponse.from() 호출 시 필요한 정보 미리 로드
        chatRoom.getConsultRequest().getMember().getId();
        chatRoom.getConsultRequest().getMember().getName();
        chatRoom.getConsultRequest().getInstitution().getId();
        chatRoom.getConsultRequest().getInstitution().getName();
        
        return chatRoom;
    }

    /**
     * 상담 종료
     * - 회원 또는 기관 관리자가 상담 종료 가능
     * - ConsultRequest와 ChatRoom 모두 비활성화
     *
     * @param chatRoomId 채팅방 ID
     * @param userId 요청자 ID
     * @param userType 요청자 유형
     */
    @Transactional
    public void closeChat(Long chatRoomId, Long userId, SenderType userType) {
        // 1. 채팅방 조회 및 권한 검증
        ChatRoom chatRoom = getChatRoomWithPermission(chatRoomId, userId, userType);

        // 2. 이미 종료된 상담인지 확인
        if (!chatRoom.getIsActive()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ALREADY_CLOSED);
        }

        // 3. ConsultRequest 종료
        ConsultRequest consultRequest = chatRoom.getConsultRequest();
        consultRequest.close();

        // 4. ChatRoom 비활성화
        chatRoom.deactivate();

        log.info("상담 종료 완료: chatRoomId={}, consultRequestId={}, userId={}, userType={}",
                chatRoomId, consultRequest.getId(), userId, userType);
    }

    /**
     * 채팅방 조회 및 권한 검증 헬퍼 메서드
     *
     * @param chatRoomId 채팅방 ID
     * @param userId 요청자 ID
     * @param userType 요청자 유형
     * @return 채팅방
     */
    private ChatRoom getChatRoomWithPermission(Long chatRoomId, Long userId, SenderType userType) {
        if (userType == SenderType.MEMBER) {
            // 회원: 본인의 채팅방인지 확인
            return chatRoomRepository.findByIdAndMemberId(chatRoomId, userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ACCESS_DENIED));
        } else if (userType == SenderType.INSTITUTION_ADMIN) {
            // 기관 관리자: 소속 기관의 채팅방인지 확인
            InstitutionAdmin admin = institutionAdminRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

            if (!admin.hasInstitution()) {
                throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
            }

            return chatRoomRepository.findByIdAndInstitutionId(chatRoomId, admin.getInstitution().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ACCESS_DENIED));
        } else {
            throw new BusinessException(ErrorCode.INVALID_SENDER_TYPE);
        }
    }

    /**
     * 발신자 권한 검증 헬퍼 메서드
     *
     * @param chatRoom 채팅방
     * @param senderType 발신자 유형
     * @param senderId 발신자 ID
     */
    private void validateSenderPermission(ChatRoom chatRoom, SenderType senderType, Long senderId) {
        if (senderType == SenderType.MEMBER) {
            // 회원: 본인의 채팅방인지 확인
            if (!chatRoom.getMemberId().equals(senderId)) {
                throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
            }
        } else if (senderType == SenderType.INSTITUTION_ADMIN) {
            // 기관 관리자: 소속 기관의 채팅방인지 확인
            InstitutionAdmin admin = institutionAdminRepository.findById(senderId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

            if (!admin.hasInstitution()) {
                throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
            }

            if (!chatRoom.getInstitutionId().equals(admin.getInstitution().getId())) {
                throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_SENDER_TYPE);
        }
    }

    /**
     * 회원의 상담 내역 목록 조회
     * - 마이페이지 상담 내역 조회용
     *
     * @param memberId 회원 ID
     * @param status 필터링할 상태 (null이면 전체)
     * @param pageable 페이징 정보
     * @return 상담 내역 목록
     */
    public ConsultRequestListResponse getMyConsultRequests(Long memberId, ConsultRequestStatus status, Pageable pageable) {
        // 1. 회원 존재 확인
        memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 상담 요청 목록 조회 (페이징)
        Page<ConsultRequest> consultRequests = consultRequestRepository.findByMemberIdWithDetailsPaged(
                memberId, status, pageable);

        // 3. ChatRoom 배치 조회 (N+1 문제 방지)
        List<Long> consultRequestIds = consultRequests.getContent().stream()
                .map(ConsultRequest::getId)
                .collect(Collectors.toList());
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByConsultRequestIdIn(consultRequestIds);
        
        // ConsultRequest ID -> ChatRoom 매핑 (빠른 조회를 위한 Map)
        Map<Long, ChatRoom> chatRoomMap = chatRooms.stream()
                .collect(Collectors.toMap(
                        room -> room.getConsultRequest().getId(),
                        room -> room,
                        (existing, replacement) -> existing
                ));

        // 4. DTO 변환
        List<ConsultRequestListResponse.ConsultRequestItem> items = consultRequests.getContent().stream()
                .map(request -> {
                    ChatRoom chatRoom = chatRoomMap.get(request.getId());

                    return ConsultRequestListResponse.ConsultRequestItem.builder()
                            .id(request.getId())
                            .chatRoomId(chatRoom != null ? chatRoom.getId() : null)
                            .institution(ConsultRequestListResponse.ConsultRequestItem.InstitutionInfo.builder()
                                    .id(request.getInstitution().getId())
                                    .name(request.getInstitution().getName())
                                    .build())
                            .counsel(ConsultRequestListResponse.ConsultRequestItem.CounselInfo.builder()
                                    .id(request.getCounsel().getId())
                                    .title(request.getCounsel().getTitle())
                                    .build())
                            .status(request.getStatus())
                            .lastMessageContent(chatRoom != null ? chatRoom.getLastMessageContent() : null)
                            .lastMessageAt(chatRoom != null ? chatRoom.getLastMessageAt() : null)
                            .createdAt(request.getCreatedAt())
                            .closedAt(request.getClosedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return ConsultRequestListResponse.of(consultRequests, items);
    }

    /**
     * 기관의 상담 요청 목록 조회
     * - 기관 관리자 상담 요청 관리용
     *
     * @param institutionId 기관 ID
     * @param adminId 기관 관리자 ID (권한 검증용)
     * @param status 필터링할 상태 (null이면 전체)
     * @param pageable 페이징 정보
     * @return 상담 요청 목록
     */
    public ConsultRequestListResponse getInstitutionConsultRequests(
            Long adminId, ConsultRequestStatus status, Pageable pageable) {
        // 1. 기관 관리자 조회 및 기관 ID 추출
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }

        Long institutionId = admin.getInstitution().getId();

        // 2. 상담 요청 목록 조회 (페이징)
        Page<ConsultRequest> consultRequests = consultRequestRepository.findByInstitutionIdWithDetailsPaged(
                institutionId, status, pageable);

        // 3. ChatRoom 배치 조회 (N+1 문제 방지)
        List<Long> consultRequestIds = consultRequests.getContent().stream()
                .map(ConsultRequest::getId)
                .collect(Collectors.toList());
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByConsultRequestIdIn(consultRequestIds);
        
        // ConsultRequest ID -> ChatRoom 매핑 (빠른 조회를 위한 Map)
        Map<Long, ChatRoom> chatRoomMap = chatRooms.stream()
                .collect(Collectors.toMap(
                        room -> room.getConsultRequest().getId(),
                        room -> room,
                        (existing, replacement) -> existing
                ));

        // 4. DTO 변환
        List<ConsultRequestListResponse.ConsultRequestItem> items = consultRequests.getContent().stream()
                .map(request -> {
                    ChatRoom chatRoom = chatRoomMap.get(request.getId());

                    return ConsultRequestListResponse.ConsultRequestItem.builder()
                            .id(request.getId())
                            .chatRoomId(chatRoom != null ? chatRoom.getId() : null)
                            .institution(ConsultRequestListResponse.ConsultRequestItem.InstitutionInfo.builder()
                                    .id(request.getInstitution().getId())
                                    .name(request.getInstitution().getName())
                                    .build())
                            .counsel(ConsultRequestListResponse.ConsultRequestItem.CounselInfo.builder()
                                    .id(request.getCounsel().getId())
                                    .title(request.getCounsel().getTitle())
                                    .build())
                            .status(request.getStatus())
                            .lastMessageContent(chatRoom != null ? chatRoom.getLastMessageContent() : null)
                            .lastMessageAt(chatRoom != null ? chatRoom.getLastMessageAt() : null)
                            .createdAt(request.getCreatedAt())
                            .closedAt(request.getClosedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return ConsultRequestListResponse.of(consultRequests, items);
    }
}

