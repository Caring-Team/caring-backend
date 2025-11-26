package com.caring.caringbackend.api.internal.reservation.dto.response;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Schema(description = "회원 예약 상세 응답 DTO")
public record MemberReservationDetailResponseDto(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "기관 ID", example = "10")
        Long institutionId,

        @Schema(description = "기관명", example = "행복요양원")
        String institutionName,

        @Schema(description = "기관 주소", example = "서울시 강남구 테헤란로 123")
        String institutionAddress,

        @Schema(description = "기관 연락처", example = "02-1234-5678")
        String institutionPhone,

        @Schema(description = "어르신 프로필 ID", example = "5")
        Long elderlyProfileId,

        @Schema(description = "어르신 이름", example = "김할머니")
        String elderlyName,

        @Schema(description = "예약 날짜", example = "2025-01-15")
        LocalDate reservationDate,

        @Schema(description = "예약 시간", example = "14:00")
        LocalTime reservationTime,

        @Schema(description = "예약 상태", example = "PENDING")
        ReservationStatus status,

        @Schema(description = "기관 설명", example = "치매 전문 요양원입니다.")
        String institutionDescription
) {
    public static MemberReservationDetailResponseDto from(Reservation reservation) {
        return MemberReservationDetailResponseDto.builder()
                .reservationId(reservation.getId())
                .institutionId(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getId())
                .institutionName(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getName())
                .institutionAddress(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getAddress().toString())
                .institutionPhone(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getPhoneNumber())
                .elderlyProfileId(reservation.getElderlyProfile().getId())
                .elderlyName(reservation.getElderlyProfile().getName())
                .reservationDate(reservation.getCounselDetail().getServiceDate())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .institutionDescription(reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getDescription())
                .build();
    }
}

