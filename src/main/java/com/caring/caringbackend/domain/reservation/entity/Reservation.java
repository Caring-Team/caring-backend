package com.caring.caringbackend.domain.reservation.entity;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 엔티티
 * <p>
 * 회원이 요양 기관의 상담 서비스를 예약한 정보를 관리합니다.
 * 예약자(회원), 대상 어르신, 예약 서비스, 예약 상태 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기관 상담 서비스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_id", nullable = false)
    private InstitutionCounsel institutionCounsel;

    // Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    // 어르신
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_profile_id", nullable = false)
    private ElderlyProfile elderlyProfile;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Builder
    public Reservation(InstitutionCounsel institutionCounsel, Member member, ElderlyProfile elderlyProfile, ReservationStatus status) {
        this.institutionCounsel = institutionCounsel;
        this.member = member;
        this.elderlyProfile = elderlyProfile;
        this.status = status;
    }

    // TODO: 필요한 도메인 로직 작성
}
