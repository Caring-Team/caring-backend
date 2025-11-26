package com.caring.caringbackend.api.internal.chat.dto.response;

import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 💬 채팅방 정보 응답 DTO
 * <p>
 * 채팅방 정보 조회 시 사용하는 응답 객체입니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfoResponse {

    /**
     * 💬 채팅방 ID
     */
    private Long chatRoomId;

    /**
     * 📋 상담 요청 ID
     */
    private Long consultRequestId;

    /**
     * 👤 회원 정보
     */
    private MemberInfo member;

    /**
     * 🏥 기관 정보
     */
    private InstitutionInfo institution;

    /**
     * 📝 마지막 메시지 내용
     */
    private String lastMessageContent;

    /**
     * 📅 마지막 메시지 시간
     */
    private LocalDateTime lastMessageAt;

    /**
     * ✅ 채팅방 활성화 여부
     */
    private Boolean isActive;

    /**
     * 📅 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 👤 회원 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String name;
    }

    /**
     * 🏥 기관 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitutionInfo {
        private Long id;
        private String name;
    }

    /**
     * 📤 ChatRoom 엔티티를 ChatRoomInfoResponse로 변환
     *
     * @param chatRoom 채팅방 엔티티
     * @return ChatRoomInfoResponse
     */
    public static ChatRoomInfoResponse from(ChatRoom chatRoom) {
        return ChatRoomInfoResponse.builder()
                .chatRoomId(chatRoom.getId())
                .consultRequestId(chatRoom.getConsultRequest().getId())
                .member(MemberInfo.builder()
                        .id(chatRoom.getConsultRequest().getMember().getId())
                        .name(chatRoom.getConsultRequest().getMember().getName())
                        .build())
                .institution(InstitutionInfo.builder()
                        .id(chatRoom.getConsultRequest().getInstitution().getId())
                        .name(chatRoom.getConsultRequest().getInstitution().getName())
                        .build())
                .lastMessageContent(chatRoom.getLastMessageContent())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .isActive(chatRoom.getIsActive())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}

