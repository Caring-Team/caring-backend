package com.caring.caringbackend.api.internal.recommendation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecommendRequestDto(
        @NotNull(message = "어르신 프로필 ID는 필수입니다")
        Long elderlyProfileId,

        @Size(max = 500, message = "추가 텍스트는 500자를 초과할 수 없습니다")
        String additionalText
) {
}