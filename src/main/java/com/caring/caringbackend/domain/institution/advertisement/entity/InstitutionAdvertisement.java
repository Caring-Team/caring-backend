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
 * 기관 광고 엔티티 (승인된 광고만)
 * <p>
 * 관리자가 승인한 광고의 게재 정보를 관리합니다.
 * REQUEST 단계는 InstitutionAdvertisementRequest 엔티티에서 관리됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionAdvertisement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "request_id")
    private Long requestId;  // 원본 신청 ID 참조

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

    @Column(length = 500)
    private String cancelReason;

    @Column(length = 500)
    private String adminMemo;

    @Builder
    public InstitutionAdvertisement(Institution institution, Long requestId, AdvertisementType type,
                                    AdvertisementStatus status, String title, String description,
                                    String bannerImageUrl, LocalDateTime startDateTime,
                                    LocalDateTime endDateTime) {
        this.institution = institution;
        this.requestId = requestId;
        this.type = type;
        this.status = status;
        this.title = title;
        this.description = description;
        this.bannerImageUrl = bannerImageUrl;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * 승인된 신청으로부터 광고 생성 (관리자 승인 시)
     */
    public static InstitutionAdvertisement createFromRequest(Institution institution, AdvertisementType type,
                                                             String title, String description, String bannerImageUrl,
                                                             LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 시작 시간에 따라 PENDING 또는 ACTIVE 상태 결정
        AdvertisementStatus initialStatus = startDateTime.isBefore(LocalDateTime.now())
                ? AdvertisementStatus.ADVERTISEMENT_ACTIVE
                : AdvertisementStatus.ADVERTISEMENT_PENDING;

        return InstitutionAdvertisement.builder()
                .institution(institution)
                .type(type)
                .status(initialStatus)
                .title(title)
                .description(description)
                .bannerImageUrl(bannerImageUrl)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    /**
     * 광고 취소 (기관)
     */
    public void cancel(String reason) {
        if (this.status == AdvertisementStatus.ADVERTISEMENT_ACTIVE) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_ACTIVE_ADVERTISEMENT);
        }
        if (this.status == AdvertisementStatus.ADVERTISEMENT_ENDED ||
                this.status == AdvertisementStatus.ADVERTISEMENT_CANCELED) {
            throw new BusinessException(ErrorCode.ADVERTISEMENT_ALREADY_FINISHED);
        }
        this.status = AdvertisementStatus.ADVERTISEMENT_CANCELED;
        this.cancelReason = reason;
    }

    /**
     * 광고 상태 업데이트 (스케줄러용)
     */
    public void updateStatus(AdvertisementStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 광고 강제 종료 (관리자용)
     */
    public void forceEnd(String reason) {
        if (this.status != AdvertisementStatus.ADVERTISEMENT_ACTIVE &&
                this.status != AdvertisementStatus.ADVERTISEMENT_PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ADVERTISEMENT_STATUS);
        }
        this.status = AdvertisementStatus.ADVERTISEMENT_ENDED;
        this.adminMemo = reason;
    }

    /**
     * 현재 활성 광고인지 확인
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == AdvertisementStatus.ADVERTISEMENT_ACTIVE &&
                now.isAfter(this.startDateTime) &&
                now.isBefore(this.endDateTime);
    }

    /**
     * 취소 가능한 상태인지 확인
     */
    public boolean isCancelable() {
        return this.status == AdvertisementStatus.ADVERTISEMENT_PENDING;
    }
}
