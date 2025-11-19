package com.caring.caringbackend.domain.institution.advertisement.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기관 광고 신청 엔티티
 * <p>
 * 기관이 신청한 광고 요청을 관리합니다.
 * 관리자의 승인/거절을 대기하는 상태입니다.
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdvertisementType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdvertisementStatus status;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    private String bannerImageUrl;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private LocalDateTime processedAt;

    @Column(length = 500)
    private String rejectionReason;

    @Column(length = 500)
    private String adminMemo;

    @Builder
    public InstitutionAdvertisementRequest(Institution institution, AdvertisementType type,
                                           AdvertisementStatus status, String title, String description,
                                           String bannerImageUrl, LocalDateTime startDateTime,
                                           LocalDateTime endDateTime) {
        this.institution = institution;
        this.type = type;
        this.status = status;
        this.title = title;
        this.description = description;
        this.bannerImageUrl = bannerImageUrl;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * 광고 신청 생성
     */
    public static InstitutionAdvertisementRequest create(Institution institution, AdvertisementType type,
                                                         String title, String description, String bannerImageUrl,
                                                         LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return InstitutionAdvertisementRequest.builder()
                .institution(institution)
                .type(type)
                .status(AdvertisementStatus.REQUEST_PENDING)
                .title(title)
                .description(description)
                .bannerImageUrl(bannerImageUrl)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    /**
     * 광고 승인 (관리자)
     */
    public void approve(String memo) {
        if (this.status != AdvertisementStatus.REQUEST_PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_STATUS);
        }
        this.status = AdvertisementStatus.REQUEST_APPROVED;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = memo;
    }

    /**
     * 광고 거절 (관리자)
     */
    public void reject(String reason) {
        if (this.status != AdvertisementStatus.REQUEST_PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_STATUS);
        }
        this.status = AdvertisementStatus.REQUEST_REJECTED;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * InstitutionAdvertisement 생성용 데이터 추출
     */
    public InstitutionAdvertisement toAdvertisement() {
        if (this.status != AdvertisementStatus.REQUEST_APPROVED) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_STATUS);
        }

        return InstitutionAdvertisement.createFromRequest(
                this.institution,
                this.type,
                this.title,
                this.description,
                this.bannerImageUrl,
                this.startDateTime,
                this.endDateTime
        );
    }

    /**
     * 승인 대기 상태인지 확인
     */
    public boolean isPending() {
        return this.status == AdvertisementStatus.REQUEST_PENDING;
    }
}
