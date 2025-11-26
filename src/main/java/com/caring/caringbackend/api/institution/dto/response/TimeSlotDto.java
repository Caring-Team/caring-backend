package com.caring.caringbackend.api.institution.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 시간대 정보 DTO
 */
@Schema(description = "시간대 정보")
public record TimeSlotDto(
        @Schema(description = "시작 시간", example = "09:00")
        String startTime,

        @Schema(description = "종료 시간", example = "09:30")
        String endTime,

        @Schema(description = "예약 가능 여부", example = "true")
        boolean isAvailable
) {
}
