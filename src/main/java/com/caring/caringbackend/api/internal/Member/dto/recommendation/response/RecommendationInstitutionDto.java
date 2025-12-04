package com.caring.caringbackend.api.internal.Member.dto.recommendation.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RecommendationInstitutionDto {
        // 기관 id
        Long institutionId;

        // 이름
        String name;

        // 메인 이미지 URL
        String mainImageUrl;

        // 유형
        String type;

        // 주소
        String address;

        // 입소 가능 여부
        Boolean isAvailable;

        // 기관의 태그들
        List<String> tags;

        // 추천의 이유 설명 텍스트
        String recommendationReason;

    public static RecommendationInstitutionDto create(
            Long institutionId,
            String name,
            String mainImageUrl,
            String type,
            String address,
            Boolean isAvailable,
            List<String> tags,
            String recommendationReason
    ) {
        return RecommendationInstitutionDto.builder()
                .institutionId(institutionId)
                .name(name)
                .mainImageUrl(mainImageUrl)
                .type(type)
                .address(address)
                .isAvailable(isAvailable)
                .tags(tags)
                .recommendationReason(recommendationReason)
                .build();
    }

}
