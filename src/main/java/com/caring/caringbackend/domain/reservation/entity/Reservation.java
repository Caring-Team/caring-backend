package com.caring.caringbackend.domain.reservation.entity;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 예약 엔티티
 * <p>
 * 회원이 요양 기관의 상담 서비스를 예약한 정보를 관리합니다.
 * 예약자(회원), 대상 어르신, 예약 서비스, 예약 상태 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약 시작 시간
    @Column(nullable = false)
    private LocalTime startTime;

    // 예약 종료 시간
    @Column(nullable = false)
    private LocalTime endTime;

    // 기관 상담 서비스 디테일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_detail_id", nullable = false)
    private InstitutionCounselDetail counselDetail;

    // Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 어르신
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_profile_id", nullable = false)
    private ElderlyProfile elderlyProfile;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    // 예약 완료 일시 (리뷰 작성 가능 기간 판정용)
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 확정된 날짜와 시간
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // 취소된 날짜와 시간
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Builder
    public Reservation(InstitutionCounselDetail counselDetail,
                       Member member,
                       ElderlyProfile elderlyProfile,
                       LocalTime startTime,
                       LocalTime endTime,
                       ReservationStatus status
    ) {
        this.counselDetail = counselDetail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.member = member;
        this.elderlyProfile = elderlyProfile;
        this.status = status;
    }

    public static Reservation createReservation(
            InstitutionCounselDetail counselDetail,
            Member member,
            ElderlyProfile elderlyProfile,
            LocalTime startTime,
            LocalTime endTime) {
        return Reservation.builder()
                .counselDetail(counselDetail)
                .member(member)
                .elderlyProfile(elderlyProfile)
                .startTime(startTime)
                .endTime(endTime)
                .status(ReservationStatus.PENDING)
                .build();
    }

    /**
     * 예약 상태 변경
     * <p>
     * 상태가 변경될 때 완료 시각을 기록합니다.
     */
    public void updateStatus(ReservationStatus newStatus) {

    }

    public void updateToConfirmed() {
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void updateToCancelled() {
        this.status = ReservationStatus.CANCELLED;
        this.canceledAt = LocalDateTime.now();
    }

    public void updateToCompleted() {
        this.status = ReservationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
