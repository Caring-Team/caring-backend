package com.caring.caringbackend.api.user.dto.review.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.lang.Nullable;

/**
 * ⭐ 리뷰 작성 요청 DTO
 * <p>
 * 리뷰 작성 시 필요한 정보를 담는 요청 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    /**
     * 📅 예약 ID (필수)
     */
    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;

    /**
     * 📝 리뷰 내용 (필수, 10~500자)
     */
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 10, max = 500, message = "리뷰 내용은 10자 이상 500자 이하여야 합니다.")
    private String content;

    /**
     * ⭐ 별점 (필수, 1~5)
     */
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
    @Column(nullable = false)
    private int rating;

    /**
     * 🏷️ 리뷰 태그 ID 목록 (선택, 최대 10개)
     */
    @Size(max = 10, message = "태그는 최대 10개까지 선택할 수 있습니다.")
    @Nullable
    private List<Long> tagIds;
}

