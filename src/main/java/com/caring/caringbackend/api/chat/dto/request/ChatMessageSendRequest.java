package com.caring.caringbackend.api.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 💬 메시지 전송 요청 DTO
 * <p>
 * 채팅 메시지 전송 시 사용하는 요청 객체입니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSendRequest {

    /**
     * 📝 메시지 내용 (필수, 1~2000자)
     */
    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 2000, message = "메시지 내용은 1자 이상 2000자 이하여야 합니다.")
    private String content;
}

