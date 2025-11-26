package com.caring.caringbackend.api.internal.institution.dto.request.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 광고 신청 요청 DTO
 */
@Schema(description = "광고 신청 요청")
public record AdvertisementCreateRequestDto(
        @Schema(description = "광고 유형", example = "MAIN_BANNER")
        @NotNull(message = "광고 유형은 필수입니다")
        AdvertisementType type,

        @Schema(description = "광고 시작 일시", example = "2025-11-20T00:00:00")
        @NotNull(message = "시작 일시는 필수입니다")
        @Future(message = "시작 일시는 현재 이후여야 합니다")
        LocalDateTime startDateTime,

        @Schema(description = "광고 종료 일시", example = "2025-11-27T23:59:59")
        @NotNull(message = "종료 일시는 필수입니다")
        @Future(message = "종료 일시는 현재 이후여야 합니다")
        LocalDateTime endDateTime,

        @Schema(description = "광고 제목", example = "서울 최고의 요양원")
        @NotBlank(message = "광고 제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다")
        String title,

        @Schema(description = "광고 설명", example = "전문 간호사 24시간 상주")
        @Size(max = 500, message = "설명은 500자 이하여야 합니다")
        String description,

        @Schema(description = "광고 배너 이미지 URL", example = "https://...")
        String bannerImageUrl
) {
    /**
     * 요청 데이터 유효성 검증
     */
    public void validate() {
        // 종료일이 시작일보다 이전인지 검증
        if (endDateTime.isBefore(startDateTime)) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_PERIOD);
        }

        // 최소 광고 기간 검증 (1일)
        long daysBetween = ChronoUnit.DAYS.between(startDateTime, endDateTime);
        if (daysBetween < 1) {
            throw new BusinessException(ErrorCode.ADVERTISEMENT_PERIOD_TOO_SHORT);
        }
    }
}
