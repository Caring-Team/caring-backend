package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Reservation Custom Repository 인터페이스
 * QueryDSL을 사용한 동적 쿼리 처리
 */
public interface ReservationRepositoryCustom {

    /**
     * 기관 ID와 필터 조건으로 예약 목록 조회 (동적 쿼리)
     *
     * @param institutionId 기관 ID
     * @param status        예약 상태 (null 가능)
     * @param startDate     시작일 (null 가능)
     * @param endDate       종료일 (null 가능)
     * @return 예약 목록
     */
    List<Reservation> findByInstitutionIdWithFilters(
            Long institutionId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}

