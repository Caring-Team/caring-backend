package com.caring.caringbackend.domain.institution.advertisement.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기관 광고 요청 엔티티
 * <p>
 * 요양 기관이 신청한 광고 요청 정보를 관리합니다.
 * 관리자의 승인 대기 중인 광고 요청의 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionAdvertisementRequest extends BaseEntity {

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

    // 요청 시작 시간
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    // 요청 종료 시간
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Builder
    public InstitutionAdvertisementRequest(Institution institution, AdvertisementType type,
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
