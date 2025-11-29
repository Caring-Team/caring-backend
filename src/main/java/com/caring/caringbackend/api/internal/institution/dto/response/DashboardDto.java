package com.caring.caringbackend.api.internal.institution.dto.response;

import lombok.Builder;

@Builder
public record DashboardDto(

        Long pendingCount,

        Long todayConfirmedCount,

        Long todayCancelledCount,

        Long recentReviewCount
) {

}
