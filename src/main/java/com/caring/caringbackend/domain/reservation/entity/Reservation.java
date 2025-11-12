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

    // 예약 시간
    @Column(nullable = false)
    private LocalTime reservationTime;

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
    private ReservationStatus status = ReservationStatus.COMPLETED;

    @Builder
    public Reservation(InstitutionCounselDetail counselDetail,
                       Member member,
                       ElderlyProfile elderlyProfile,
                       LocalTime reservationTime,
                       ReservationStatus status
    ) {
        this.counselDetail = counselDetail;
        this.reservationTime = reservationTime;
        this.member = member;
        this.elderlyProfile = elderlyProfile;
        this.status = status;
    }

    public static Reservation createReservation(InstitutionCounselDetail counselDetail, Member member, ElderlyProfile elderlyProfile, LocalTime reservationTime) {
        return Reservation.builder()

                .counselDetail(counselDetail)
                .member(member)
                .elderlyProfile(elderlyProfile)
                .reservationTime(reservationTime)
                .status(ReservationStatus.COMPLETED)
                .build();
    }

    /**
     * 예약 상태 변경
     */
    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }
}
