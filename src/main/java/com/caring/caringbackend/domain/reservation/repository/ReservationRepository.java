package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @param memberId      회원 ID
     * @return 예약
     */
    @EntityGraph(attributePaths = {"counselDetail", "counselDetail.institutionCounsel", "counselDetail.institutionCounsel.institution", "elderlyProfile"})
    Optional<Reservation> findByIdAndMemberId(Long reservationId, Long memberId);

    /**
     * 회원 ID로 예약 목록 조회 (상세 정보 포함)
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 예약 페이지
     */
    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.counselDetail cd
            JOIN FETCH cd.institutionCounsel ic
            JOIN FETCH ic.institution i
            JOIN FETCH r.elderlyProfile ep
            WHERE r.member.id = :memberId
            ORDER BY cd.serviceDate DESC, r.createdAt DESC
            """)
    Page<Reservation> findByMemberIdWithDetails(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    /**
     * 회원 ID와 예약 상태 목록으로 활성 예약 존재 여부 확인
     *
     * @param memberId 회원 ID
     * @param statuses 예약 상태 목록
     * @return 활성 예약 존재 여부
     */
    boolean existsByMemberIdAndStatusIn(Long memberId, List<ReservationStatus> statuses);

    /**
     * 기관 ID와 필터 조건으로 예약 목록 조회
     *
     * @param institutionId 기관 ID
     * @param status        예약 상태 (null 가능)
     * @param startDate     시작일 (null 가능)
     * @param endDate       종료일 (null 가능)
     * @return 예약 페이지
     */
    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.counselDetail cd
            JOIN FETCH cd.institutionCounsel ic
            JOIN FETCH ic.institution i
            WHERE i.id = :institutionId
            AND (:status IS NULL OR r.status = :status)
            AND (:startDate IS NULL OR cd.serviceDate >= :startDate)
            AND (:endDate IS NULL OR cd.serviceDate <= :endDate)
            ORDER BY cd.serviceDate DESC, r.createdAt DESC
            """)
    List<Reservation> findByInstitutionIdWithFilters(
            @Param("institutionId") Long institutionId,
            @Param("status") ReservationStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select 
                sum(case when r.status = 'PENDING' then 1 else 0 end) as pendingCount,
                sum(case when r.status = 'CONFIRMED' 
                          and r.confirmedAt between :startOfToday and :endOfToday 
                         then 1 else 0 end) as todayConfirmedCount,
                sum(case when r.status = 'CANCELLED' 
                          and r.canceledAt between :startOfToday and :endOfToday
                         then 1 else 0 end) as todayCancelledCount                
            from Reservation r
            join r.counselDetail cd
            join cd.institutionCounsel ic
            join ic.institution i
            where i.id = :institutionId
            """)
    ReservationStatsProjection countReservationsByStatusAndInstitution(
            @Param("institutionId") Long institutionId,
            @Param("startOfToday") LocalDateTime startOfToday,
            @Param("endOfToday") LocalDateTime endOfToday
    );
}