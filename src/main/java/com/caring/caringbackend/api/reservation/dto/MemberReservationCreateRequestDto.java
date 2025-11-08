package com.caring.caringbackend.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberReservationCreateRequestDto {

    // 기관 상담, 날짜, 예약 시간, erderlyProfile,
    @Schema(description = "상담 서비스 ID", example = "1")
    @NotNull(message = "counselId는 필수입니다")
    private Long counselId;

    @Schema(description = "예약 날짜", example = "2025-11-11")
    @NotNull(message = "reservationDate는 필수입니다")
    private LocalDate reservationDate;

    @Schema(description = "슬롯 인덱스 (0~47)", example = "18")
    int slotIndex;

    @Schema(description = "시작 시간", example = "09:00")
    @NotNull(message = "startTime는 필수입니다")
    LocalTime startTime;

    @Schema(description = "종료 시간", example = "09:30")
    LocalTime endTime;

    @Schema(description = "어르신 프로필 ID", example = "1")
    Long elderlyProfileId;
}
