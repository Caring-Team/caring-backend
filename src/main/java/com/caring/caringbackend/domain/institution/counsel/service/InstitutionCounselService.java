package com.caring.caringbackend.domain.institution.counsel.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselCreateRequestDto;

public interface InstitutionCounselService {
    void createInstitutionCounsel(Long adminId, Long institutionId, InstitutionCounselCreateRequestDto requestDto);
}
