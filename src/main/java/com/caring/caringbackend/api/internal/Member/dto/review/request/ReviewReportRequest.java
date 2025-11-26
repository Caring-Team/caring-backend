package com.caring.caringbackend.api.internal.Member.dto.review.request;

import com.caring.caringbackend.domain.review.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ⭐ 리뷰 신고 요청 DTO
 * <p>
 * 리뷰 신고 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportRequest {

    /**
     * 🚨 신고 사유 (필수)
     */
    @NotNull(message = "신고 사유는 필수입니다.")
    private ReportReason reportReason;

    /**
     * 📝 상세 설명 (선택, 최대 500자)
     */
    @Size(max = 500, message = "상세 설명은 500자 이하여야 합니다.")
    private String description;
}

