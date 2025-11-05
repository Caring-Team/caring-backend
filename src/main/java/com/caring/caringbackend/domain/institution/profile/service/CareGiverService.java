package com.caring.caringbackend.domain.institution.profile.service;


import com.caring.caringbackend.api.institution.dto.request.CareGiverCreateRequestDto;

public interface CareGiverService {

    // 요양 보호사 등록
    public void registerCareGiver(Long adminId, Long institutionId, CareGiverCreateRequestDto requestDto);
}
