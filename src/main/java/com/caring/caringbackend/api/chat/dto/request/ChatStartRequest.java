package com.caring.caringbackend.api.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 💬 상담 시작 요청 DTO
 * <p>
 * 회원이 '상담 시작' 버튼 클릭 시 사용하는 요청 객체입니다.
 * ConsultRequest와 ChatRoom이 동시에 생성됩니다.
 *
 * @author 윤다인
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatStartRequest {

    /**
     * 🏥 기관 ID (필수)
     */
    @NotNull(message = "기관 ID는 필수입니다.")
    private Long institutionId;

    /**
     * 💬 상담 서비스 ID (필수)
     */
    @NotNull(message = "상담 서비스 ID는 필수입니다.")
    private Long counselId;
}

