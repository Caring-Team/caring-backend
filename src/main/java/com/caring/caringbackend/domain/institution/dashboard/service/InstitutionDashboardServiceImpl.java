package com.caring.caringbackend.domain.institution.dashboard.service;

import com.caring.caringbackend.api.internal.institution.dto.response.DashboardDto;
import com.caring.caringbackend.domain.reservation.repository.ReservationStatsProjection;
import com.caring.caringbackend.domain.reservation.service.InstitutionReservationService;
import com.caring.caringbackend.domain.review.service.InstitutionReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstitutionDashboardServiceImpl implements InstitutionDashboardService {

    private final InstitutionReservationService institutionReservationService;
    private final InstitutionReviewService institutionReviewService;

    public DashboardDto getDashboardData(Long institutionId) {
        ReservationStatsProjection reservationStats = institutionReservationService.getReservationStatusCounts(institutionId);
        Long recentReviewCount = institutionReviewService.getRecentReviewCount(institutionId);

        return DashboardDto.builder()
                .pendingCount(reservationStats.getPendingCount())
                .todayConfirmedCount(reservationStats.getTodayConfirmedCount())
                .todayCancelledCount(reservationStats.getTodayCancelledCount())
                .recentReviewCount(recentReviewCount)
                .build();
    }
}
