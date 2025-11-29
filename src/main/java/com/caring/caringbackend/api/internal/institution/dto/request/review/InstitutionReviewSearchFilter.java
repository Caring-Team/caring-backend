package com.caring.caringbackend.api.internal.institution.dto.request.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기관 리뷰 검색 필터 DTO")
public class InstitutionReviewSearchFilter {

    @Schema(
            description = "며칠 전부터의 리뷰를 조회할지 설정하는 필드 (null이면 전체 조회)",
            example = "30",
            nullable = true
    )
    private Integer daysAgo;
}
