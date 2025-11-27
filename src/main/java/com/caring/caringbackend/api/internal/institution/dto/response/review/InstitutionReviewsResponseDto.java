package com.caring.caringbackend.api.internal.institution.dto.response.review;

import java.util.List;

public record InstitutionReviewsResponseDto(
        List<InstitutionReviewResponseDto> reviews,
        int totalCount
) {
    public static InstitutionReviewsResponseDto of(List<InstitutionReviewResponseDto> reviews) {
        return new InstitutionReviewsResponseDto(reviews, reviews.size());
    }
}
