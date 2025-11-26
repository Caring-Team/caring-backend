package com.caring.caringbackend.api.internal.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    // 성공 여부
    private boolean success;

    // 추천된 기관 dto 리스트
    private List<RecommendationInstitutionDto> institutions;

    // 추천된 기관 개수
    private int totalCount;
}
