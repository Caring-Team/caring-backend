package com.caring.caringbackend.domain.institution.dashboard.service;

import com.caring.caringbackend.api.internal.institution.dto.response.DashboardDto;

public interface InstitutionDashboardService {

    DashboardDto getDashboardData(Long adminId);
}
