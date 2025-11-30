package com.caring.caringbackend.domain.institution.counsel.service;

import com.caring.caringbackend.api.internal.institution.dto.request.counsel.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.counsel.InstitutionCounselUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.counsel.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.counsel.InstitutionCounselReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.counsel.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselStatus;

import java.time.LocalDate;
import java.util.List;

public interface InstitutionCounselService {
    void createInstitutionCounsel(Long adminId, InstitutionCounselCreateRequestDto requestDto);

    List<InstitutionCounselResponseDto> getInstitutionCounselsByAdminId(Long adminId);

    List<InstitutionCounselResponseDto> getInstitutionCounselsByInstitutionId(Long institutionId);

    CounselStatus toggleInstitutionCounselStatus(Long adminId, Long counselId);

    void deleteCounselByCounselId(Long adminId, Long counselId);

    InstitutionCounselDetailResponseDto getCounselDetail(Long adminId, Long counselId);

    InstitutionCounselReservationDetailResponseDto getOrCreateCounselDetail(Long counselId, LocalDate date);

    void updateInstitutionCounsel(Long adminId, Long counselId, InstitutionCounselUpdateRequestDto requestDto);
}
