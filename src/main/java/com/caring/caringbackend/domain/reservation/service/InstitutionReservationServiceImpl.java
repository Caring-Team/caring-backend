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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Page<InstitutionReservationResponseDto> getMyInstitutionReservations(
            Long adminId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        // adminId로 institutionId 조회
        Long institutionId = getInstitutionIdByAdminId(adminId);

        Page<Reservation> reservations = reservationRepository.findByInstitutionIdWithFilters(institutionId, status, startDate, endDate, pageable);

        return reservations.map(InstitutionReservationResponseDto::from);
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
        reservation.updateStatus(status);

        return InstitutionReservationDetailResponseDto.from(reservation);
    }

    // 내 기관 상태별 예약 개수 조회
    @Override
    public ReservationStatsProjection getReservationStatusCounts(Long institutionId) {
        return reservationRepository.countReservationsByStatusAndInstitution(institutionId);
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
}

