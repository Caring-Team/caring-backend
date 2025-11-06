package com.caring.caringbackend.domain.institution.counsel.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;

import java.time.LocalDate;
import java.util.List;

public interface InstitutionCounselService {
    void createInstitutionCounsel(Long adminId, Long institutionId, InstitutionCounselCreateRequestDto requestDto);

    List<InstitutionCounselResponseDto> getInstitutionCounsels(Long institutionId);

    CounselStatus toggleInstitutionCounselStatus(Long adminId, Long institutionId, Long counselId);

    void deleteCounselByCouncelId(Long adminId, Long institutionId, Long counselId);

    InstitutionCounselDetailResponseDto getOrCreateCounselDetail(Long counselId, LocalDate date);
}
