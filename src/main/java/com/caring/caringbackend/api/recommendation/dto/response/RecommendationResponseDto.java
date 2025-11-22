package com.caring.caringbackend.api.recommendation.dto.response;

import java.util.List;

public record RecommendationResponseDto(

        // 추천된 기관 dto 리스트
        List<RecommendationInstitutionDto> institution,

        // 추천된 기관 개수
        int totalCount
) {
    public static RecommendationResponseDto create(List<RecommendationInstitutionDto> institution, int totalCount) {
        return new RecommendationResponseDto(institution, totalCount);
    }
}
