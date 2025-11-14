package com.caring.caringbackend.api.institution.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

/**
 * 시간대 정보 DTO
 */
@Schema(description = "시간대 정보")
public record TimeSlotDto(
        @Schema(description = "슬롯 인덱스 (0~47)", example = "18")
        int slotIndex,

        @Schema(description = "시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "09:30")
        LocalTime endTime,

        @Schema(description = "예약 가능 여부", example = "true")
        boolean isAvailable
) {

    @Schema(description = "시간대 표시 문자열", example = "09:00-09:30")
    public String getTimeRange() {
        return String.format("%s-%s", startTime, endTime);
    }
}
