package com.caring.caringbackend.api.internal.reservation.dto.response;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "기관 예약 상세 조회 응답 DTO")
public record InstitutionReservationDetailResponseDto(
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

        @Schema(description = "상담 서비스 정보")
        CounselInfo counselInfo,

        @Schema(description = "회원 정보")
        MemberInfo memberInfo,

        @Schema(description = "어르신 정보")
        ElderlyInfo elderlyInfo,

        @Schema(description = "예약 생성일시", example = "2025-11-06T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "예약 수정일시", example = "2025-11-06T10:30:00")
        LocalDateTime updatedAt
) {
    public static InstitutionReservationDetailResponseDto from(Reservation reservation) {
        return new InstitutionReservationDetailResponseDto(
                reservation.getId(),
                reservation.getCounselDetail().getServiceDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                CounselInfo.from(reservation.getCounselDetail().getInstitutionCounsel()),
                MemberInfo.from(reservation.getMember()),
                ElderlyInfo.from(reservation.getElderlyProfile()),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    @Schema(description = "상담 서비스 정보")
    public record CounselInfo(
            @Schema(description = "상담 서비스 ID", example = "1")
            Long counselId,

            @Schema(description = "상담 서비스 이름", example = "초기 상담")
            String title,

            @Schema(description = "상담 서비스 설명", example = "첫 방문 시 진행되는 상담입니다")
            String description
    ) {
        public static CounselInfo from(com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel counsel) {
            return new CounselInfo(
                    counsel.getId(),
                    counsel.getTitle(),
                    counsel.getDescription()
            );
        }
    }

    @Schema(description = "회원 정보")
    public record MemberInfo(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,

            @Schema(description = "회원 이름", example = "홍길동")
            String name,

            @Schema(description = "연락처", example = "010-1234-5678")
            String phoneNumber
    ) {
        public static MemberInfo from(com.caring.caringbackend.domain.user.guardian.entity.Member member) {
            return new MemberInfo(
                    member.getId(),
                    member.getName(),
                    member.getPhoneNumber()
            );
        }
    }

    @Schema(description = "어르신 정보")
    public record ElderlyInfo(
            @Schema(description = "어르신 프로필 ID", example = "1")
            Long elderlyId,

            @Schema(description = "어르신 이름", example = "김영희")
            String name,

            @Schema(description = "연락처", example = "010-9876-5432")
            String phoneNumber
    ) {
        public static ElderlyInfo from(com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile elderly) {
            return new ElderlyInfo(
                    elderly.getId(),
                    elderly.getName(),
                    elderly.getPhoneNumber()
            );
        }
    }
}

