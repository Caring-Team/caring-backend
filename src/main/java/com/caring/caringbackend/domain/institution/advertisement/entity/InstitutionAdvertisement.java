package com.caring.caringbackend.domain.institution.advertisement.entity;

import com.caring.caringbackend.domain.institution.entity.Institution;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기관 광고 엔티티
 * <p>
 * 요양 기관의 활성화된 광고 정보를 관리합니다.
 * 승인된 광고의 게재 기간, 유형, 상태를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionAdvertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 홍보 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementType type;

    // 기관 홍보 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementStatus status;

    // 시작 시간
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    // 종료 시간
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Builder
    public InstitutionAdvertisement(Institution institution, AdvertisementType type,
                                    AdvertisementStatus status, LocalDateTime startDateTime,
                                    LocalDateTime endDateTime) {
        this.institution = institution;
        this.type = type;
        this.status = status;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // TODO: 필요한 도메인 로직 작성
}
