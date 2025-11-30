package com.caring.caringbackend.domain.institution.dashboard.service;

import com.caring.caringbackend.api.internal.institution.dto.response.DashboardDto;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.reservation.repository.ReservationStatsProjection;
import com.caring.caringbackend.domain.reservation.service.InstitutionReservationService;
import com.caring.caringbackend.domain.review.service.InstitutionReviewService;
import com.caring.caringbackend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.caring.caringbackend.global.exception.ErrorCode.ADMIN_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InstitutionDashboardServiceImpl implements InstitutionDashboardService {

    private final InstitutionAdminRepository institutionAdminRepository;
    private final InstitutionReservationService institutionReservationService;
    private final InstitutionReviewService institutionReviewService;

    public DashboardDto getDashboardData(Long adminId) {

        InstitutionAdmin admin = institutionAdminRepository.findByIdWithInstitution(adminId)
                .orElseThrow(() -> new BusinessException(ADMIN_NOT_FOUND));

        Long institutionId = admin.getInstitution().getId();
        ReservationStatsProjection reservationStats = institutionReservationService.getReservationStatusCounts(institutionId);
        Long recentReviewCount = institutionReviewService.getRecentReviewCount(institutionId);

        return DashboardDto.builder()
                .pendingCount(reservationStats.getPendingCount())
                .todayConfirmedCount(reservationStats.getTodayConfirmedCount())
                .todayCanceledCount(reservationStats.getTodayCanceledCount())
                .recentReviewCount(recentReviewCount)
                .build();
    }
}
