package com.caring.caringbackend.api.chat.dto.response;

import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 💬 상담 시작 응답 DTO
 * <p>
 * 상담 시작 성공 시 반환하는 응답 객체입니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatStartResponse {

    /**
     * 📋 상담 요청 ID
     */
    private Long consultRequestId;

    /**
     * 💬 채팅방 ID
     */
    private Long chatRoomId;

    /**
     * 🏥 기관명
     */
    private String institutionName;

    /**
     * 📤 ChatRoom 엔티티를 ChatStartResponse로 변환
     *
     * @param chatRoom 채팅방 엔티티
     * @return ChatStartResponse
     */
    public static ChatStartResponse from(ChatRoom chatRoom) {
        return ChatStartResponse.builder()
                .consultRequestId(chatRoom.getConsultRequest().getId())
                .chatRoomId(chatRoom.getId())
                .institutionName(chatRoom.getInstitutionName())
                .build();
    }
}

