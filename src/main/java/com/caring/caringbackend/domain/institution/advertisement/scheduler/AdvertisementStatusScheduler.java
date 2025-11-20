package com.caring.caringbackend.domain.institution.advertisement.scheduler;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import com.caring.caringbackend.domain.institution.advertisement.repository.InstitutionAdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 광고 상태 자동 전환 스케줄러
 * <p>
 * 5분마다 실행되어 광고 상태를 자동으로 업데이트합니다:
 * 1. PENDING → ACTIVE (시작 시간 도래)
 * 2. ACTIVE → ENDED (종료 시간 도래)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvertisementStatusScheduler {

    private final InstitutionAdvertisementRepository advertisementRepository;

    /**
     * 광고 상태 자동 업데이트
     * 5분마다 실행
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void updateAdvertisementStatus() {
        log.info("광고 상태 자동 업데이트 시작");

        LocalDateTime now = LocalDateTime.now();

        // 1. PENDING → ACTIVE (시작 시간 도래)
        int activatedCount = activatePendingAdvertisements(now);

        // 2. ACTIVE → ENDED (종료 시간 도래)
        int endedCount = endActiveAdvertisements(now);

        log.info("광고 상태 자동 업데이트 완료 - 활성화: {}건, 종료: {}건", activatedCount, endedCount);
    }

    /**
     * PENDING 상태의 광고를 ACTIVE로 전환
     */
    private int activatePendingAdvertisements(LocalDateTime now) {
        List<InstitutionAdvertisement> pendingAds = advertisementRepository.findByStatusAndStartDateTimeBefore(
                AdvertisementStatus.ADVERTISEMENT_PENDING,
                now
        );

        if (pendingAds.isEmpty()) {
            log.debug("활성화할 광고 없음");
            return 0;
        }

        pendingAds.forEach(ad -> {
            log.info("광고 활성화 - adId: {}, institutionId: {}, title: {}",
                    ad.getId(), ad.getInstitution().getId(), ad.getTitle());
            ad.updateStatus(AdvertisementStatus.ADVERTISEMENT_ACTIVE);
        });

        return pendingAds.size();
    }

    /**
     * ACTIVE 상태의 광고를 ENDED로 전환
     */
    private int endActiveAdvertisements(LocalDateTime now) {
        List<InstitutionAdvertisement> activeAds = advertisementRepository.findByStatusAndEndDateTimeBefore(
                AdvertisementStatus.ADVERTISEMENT_ACTIVE,
                now
        );

        if (activeAds.isEmpty()) {
            log.debug("종료할 광고 없음");
            return 0;
        }

        activeAds.forEach(ad -> {
            log.info("광고 종료 - adId: {}, institutionId: {}, title: {}",
                    ad.getId(), ad.getInstitution().getId(), ad.getTitle());
            ad.updateStatus(AdvertisementStatus.ADVERTISEMENT_ENDED);
        });

        return activeAds.size();
    }
}

