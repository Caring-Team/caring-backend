package com.caring.caringbackend.domain.institution.counsel.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 기관 상담 서비스 날짜별 상세 정보 엔티티

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionCounselDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_id", nullable = false)
    private InstitutionCounsel institutionCounsel;

    // 상담 서비스 제공 날짜
    @Column(nullable = false)
    private LocalDate serviceDate;

    // 상담 날짜별 30분 단위로 비트 마스킹 처리 컬럼
    // 예시) (첫 48비트 사용, 0~23시 30분 단위)
    @Column(nullable = false)
    private Long timeSlotsBitmask;

    @Builder
    public InstitutionCounselDetail(InstitutionCounsel institutionCounsel, LocalDate serviceDate, Long timeSlotsBitmask) {
        this.institutionCounsel = institutionCounsel;
        this.serviceDate = serviceDate;
        this.timeSlotsBitmask = timeSlotsBitmask;
    }

    // TODO: 필요한 도메인 로직 작성
}
