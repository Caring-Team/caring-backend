package com.caring.caringbackend.api.recommendation.dto.request;

public record RecommendRequestDto(
        Long elderlyProfileId,
        String additionalText
) {
}
