package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationResponseDto;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 기관 예약 관리 서비스 인터페이스
 */
public interface InstitutionReservationService {

    /**
     * 내 기관의 예약 목록 조회
     *
     * @param adminId 기관 관리자 ID
     * @param status 예약 상태 필터 (선택)
     * @param startDate 시작 날짜 필터 (선택)
     * @param endDate 종료 날짜 필터 (선택)
     * @return 예약 목록
     */
    List<InstitutionReservationResponseDto> getMyInstitutionReservations(
            Long adminId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * 내 기관 예약 상세 조회
     *
     * @param adminId 기관 관리자 ID
     * @param reservationId 예약 ID
     * @return 예약 상세 정보
     */
    InstitutionReservationDetailResponseDto getMyInstitutionReservationDetail(
            Long adminId,
            Long reservationId
    );

    /**
     * 내 기관 예약 상태 변경
     *
     * @param adminId 기관 관리자 ID
     * @param reservationId 예약 ID
     * @param status 변경할 상태
     * @return 변경된 예약 정보
     */
    InstitutionReservationDetailResponseDto updateMyInstitutionReservationStatus(
            Long adminId,
            Long reservationId,
            ReservationStatus status
    );

    ReservationStatsProjection getReservationStatusCounts(Long institutionId);
}
