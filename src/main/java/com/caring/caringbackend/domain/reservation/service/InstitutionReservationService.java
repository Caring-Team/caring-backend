package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.reservation.dto.response.InstitutionReservationDetailResponseDto;
import com.caring.caringbackend.api.reservation.dto.response.InstitutionReservationResponseDto;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

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
     * @param pageable 페이징 정보
     * @return 예약 목록
     */
    Page<InstitutionReservationResponseDto> getMyInstitutionReservations(
            Long adminId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
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

