package com.caring.caringbackend.api.internal.reservation.dto.response;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "기관 예약 목록 조회 응답 DTO")
public record InstitutionReservationResponseDto(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "예약 날짜", example = "2025-11-11")
        LocalDate reservationDate,

        @Schema(description = "예약 시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "예약 종료 시간", example = "09:30")
        LocalTime endTime,

        @Schema(description = "예약 상태", example = "COMPLETED")
        ReservationStatus status,

        @Schema(description = "상담 서비스 이름", example = "초기 상담")
        String counselTitle,

        @Schema(description = "회원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "어르신 이름", example = "김영희")
        String elderlyName,

        @Schema(description = "예약 생성일시", example = "2025-11-06T10:30:00")
        LocalDateTime createdAt
) {
    public static InstitutionReservationResponseDto from(Reservation reservation) {
        return new InstitutionReservationResponseDto(
                reservation.getId(),
                reservation.getCounselDetail().getServiceDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getCounselDetail().getInstitutionCounsel().getTitle(),
                reservation.getMember().getName(),
                reservation.getElderlyProfile().getName(),
                reservation.getCreatedAt()
        );
    }
}
