package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationResponseDto;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.reservation.repository.ReservationStatsProjection;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 기관 예약 관리 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionReservationServiceImpl implements InstitutionReservationService {

    private final ReservationRepository reservationRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    @Override
    public List<InstitutionReservationResponseDto> getMyInstitutionReservations(
            Long adminId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // adminId로 institutionId 조회
        Long institutionId = getInstitutionIdByAdminId(adminId);

        List<Reservation> reservations = reservationRepository.findByInstitutionIdWithFilters(institutionId, status, startDate, endDate);
        return reservations.stream()
                .map(InstitutionReservationResponseDto::from)
                .toList();
    }

    @Override
    public InstitutionReservationDetailResponseDto getMyInstitutionReservationDetail(
            Long adminId,
            Long reservationId) {

        // adminId로 institutionId 조회
        Long institutionId = getInstitutionIdByAdminId(adminId);
        Reservation reservation = getReservation(reservationId);

        // 기관 소유 확인
        validateInstitutionOwnership(reservation, institutionId);

        return InstitutionReservationDetailResponseDto.from(reservation);
    }

    @Override
    @Transactional
    public InstitutionReservationDetailResponseDto updateMyInstitutionReservationStatus(
            Long adminId,
            Long reservationId,
            ReservationStatus status) {

        // adminId로 institutionId 조회
        Long institutionId = getInstitutionIdByAdminId(adminId);
        Reservation reservation = getReservation(reservationId);

        // 기관 소유 확인
        validateInstitutionOwnership(reservation, institutionId);

        // 확정 처리
        if (status == ReservationStatus.CONFIRMED) {
            validateAndConfirmReservation(reservation);
        }
        // 취소 처리
        if (status == ReservationStatus.CANCELED) {
            validateAndCancelReservation(reservation);
        }
        // 완료 처리
        if (status == ReservationStatus.COMPLETED) {
            validateAndCompleted(reservation);
        }
        return InstitutionReservationDetailResponseDto.from(reservation);
    }

    // 내 기관 상태별 예약 개수 조회
    @Override
    public ReservationStatsProjection getReservationStatusCounts(Long institutionId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.plusDays(1).atStartOfDay();
        return reservationRepository.countReservationsByStatusAndInstitution(
                institutionId,
                startOfToday,
                endOfToday
        );
    }

    // ======================== private methods ========================

    /**
     * adminId로 institutionId 가져오기
     */
    private Long getInstitutionIdByAdminId(Long adminId) {
        InstitutionAdmin admin = institutionAdminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (!admin.hasInstitution()) {
            throw new BusinessException(ErrorCode.ADMIN_HAS_NO_INSTITUTION);
        }

        return admin.getInstitution().getId();
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    /**
     * 기관 소유 확인
     */
    private void validateInstitutionOwnership(Reservation reservation, Long institutionId) {
        if (!reservation.getCounselDetail().getInstitutionCounsel().getInstitution().getId().equals(institutionId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_INSTITUTION_ACCESS);
        }
    }

    private static void validateAndCompleted(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS_TRANSITION);
        }

        reservation.updateToCompleted();
    }

    private static void validateAndCancelReservation(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS_TRANSITION);
        }

        reservation.updateToCanceled();
    }

    private static void validateAndConfirmReservation(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS_TRANSITION);
        }

        reservation.updateToConfirmed();
    }
}

