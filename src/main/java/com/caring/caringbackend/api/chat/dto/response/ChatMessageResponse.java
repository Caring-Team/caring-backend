package com.caring.caringbackend.api.chat.dto.response;

import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import com.caring.caringbackend.domain.chat.entity.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 💬 채팅 메시지 응답 DTO
 * <p>
 * 채팅 메시지 조회 시 사용하는 응답 객체입니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    /**
     * 🔢 메시지 ID
     */
    private Long id;

    /**
     * 💬 채팅방 ID
     */
    private Long chatRoomId;

    /**
     * 👤 발신자 유형 (MEMBER, INSTITUTION_ADMIN)
     */
    private SenderType senderType;

    /**
     * 🔢 발신자 ID
     */
    private Long senderId;

    /**
     * 👤 발신자 이름
     * - 회원: 회원 이름
     * - 기관 관리자: 기관명
     */
    private String senderName;

    /**
     * 📝 메시지 내용
     */
    private String content;

    /**
     * 📅 전송 시간
     */
    private LocalDateTime createdAt;

    /**
     * 📤 ChatMessage 엔티티를 ChatMessageResponse로 변환
     *
     * @param message 메시지 엔티티
     * @param senderName 발신자 이름 (회원명 또는 기관명)
     * @return ChatMessageResponse
     */
    public static ChatMessageResponse from(ChatMessage message, String senderName) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderType(message.getSenderType())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

