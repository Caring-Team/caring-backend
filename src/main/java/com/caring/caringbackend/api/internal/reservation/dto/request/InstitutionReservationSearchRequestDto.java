package com.caring.caringbackend.api.internal.reservation.dto.request;

import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기관 예약 목록 조회 요청 DTO")
public class InstitutionReservationSearchRequestDto {

    @Schema(description = "예약 상태 필터", example = "COMPLETED")
    private ReservationStatus status;

    @Schema(description = "시작 날짜 (yyyy-MM-dd)", example = "2025-11-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "종료 날짜 (yyyy-MM-dd)", example = "2025-11-30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}

