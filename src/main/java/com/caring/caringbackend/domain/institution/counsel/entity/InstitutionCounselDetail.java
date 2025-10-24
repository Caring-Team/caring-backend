package com.caring.caringbackend.domain.institution.counsel.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 기관 상담 서비스 상세 일정 엔티티
 * <p>
 * 기관 상담 서비스의 날짜별 시간대 예약 가능 여부를 관리합니다.
 * 비트마스크를 사용하여 30분 단위로 예약 가능 시간을 효율적으로 저장합니다.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionCounselDetail extends BaseEntity {

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
