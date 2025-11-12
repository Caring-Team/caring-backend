package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 예약 Repository 인터페이스
 * <p>
 * Reservation 엔티티에 대한 데이터 액세스 계층입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 예약 ID와 회원 ID로 예약 조회
     *
     * @param reservationId 예약 ID
     * @param memberId 회원 ID
     * @return 예약
     */
    @EntityGraph(attributePaths = {"institutionCounsel", "institutionCounsel.institution"})
    Optional<Reservation> findByIdAndMemberId(Long reservationId, Long memberId);

    /**
     * 회원 ID와 예약 상태 목록으로 활성 예약 존재 여부 확인
     *
     * @param memberId 회원 ID
     * @param statuses 예약 상태 목록
     * @return 활성 예약 존재 여부
     */
    boolean existsByMemberIdAndStatusIn(Long memberId, List<ReservationStatus> statuses);
}

