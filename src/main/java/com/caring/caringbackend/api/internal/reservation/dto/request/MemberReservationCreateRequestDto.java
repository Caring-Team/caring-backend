package com.caring.caringbackend.api.internal.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Schema(description = "시작 시간", example = "09:00")
    @NotNull(message = "startTime는 필수입니다")
    LocalTime startTime;


    @Schema(description = "종료 시간", example = "09:30")
    @NotNull(message = "endTime는 필수입니다")
    LocalTime endTime;


    @Schema(description = "어르신 프로필 ID", example = "1")
    @NotNull(message = "어르신 프로필 ID는 필수입니다")
    Long elderlyProfileId;
}
