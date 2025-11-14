package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * 예약 Repository Custom 인터페이스
 */
public interface ReservationRepositoryCustom {

    /**
     * 기관의 예약 목록 조회 (동적 쿼리)
     */
    Page<Reservation> findByInstitutionIdWithFilters(
            Long institutionId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}

