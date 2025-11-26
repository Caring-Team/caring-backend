package com.caring.caringbackend.api.internal.recommendation.dto.response;

import java.util.List;

public record RecommendationInstitutionDto(
        // 기관 id
        Long institutionId,

        // 이름
        String name,

        // 유형
        String type,

        // 주소
        String address,

        // 입소 가능 여부
        Boolean isAvailable,

        // 기관의 태그들
        List<String> tags,

        // 추천의 이유 설명 텍스트
        String recommendationReason
) {
    public static RecommendationInstitutionDto create(
            Long institutionId,
            String name,
            String type,
            String address,
            Boolean isAvailable,
            List<String> tags,
            String recommendationReason
    ) {
        return new RecommendationInstitutionDto(
                institutionId,
                name,
                type,
                address,
                isAvailable,
                tags,
                recommendationReason
        );
    }
}
