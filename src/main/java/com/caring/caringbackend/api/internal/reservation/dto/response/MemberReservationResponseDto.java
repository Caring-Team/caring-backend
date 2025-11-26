package com.caring.caringbackend.api.internal.reservation.dto.response;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Schema(description = "회원 예약 목록 응답 DTO")
public record MemberReservationResponseDto(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "기관명", example = "행복요양원")
        String institutionName,

        @Schema(description = "어르신 이름", example = "김할머니")
        String elderlyName,

        @Schema(description = "예약 날짜", example = "2025-01-15")
        LocalDate reservationDate,

        @Schema(description = "예약 시간", example = "14:00")
        LocalTime reservationTime,

        @Schema(description = "예약 상태", example = "PENDING")
        ReservationStatus status,

        @Schema(description = "기관 연락처", example = "02-1234-5678")
        String institutionPhone
) {
    public static MemberReservationResponseDto from(Reservation reservation) {
        return MemberReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .institutionName(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getName())
                .elderlyName(reservation.getElderlyProfile().getName())
                .reservationDate(reservation.getCounselDetail().getServiceDate())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .institutionPhone(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getPhoneNumber())
                .build();
    }
}

