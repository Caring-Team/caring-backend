package com.caring.caringbackend.domain.institution.profile.service;


import com.caring.caringbackend.api.institution.dto.request.CareGiverCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.CareGiverResponseDto;

import java.util.List;

public interface CareGiverService {

    /**
     * 요양보호사 등록
     */
    void registerCareGiver(Long adminId, Long institutionId, CareGiverCreateRequestDto requestDto);

    /**
     * 기관별 요양보호사 목록 조회
     */
    List<CareGiverResponseDto> getCareGiversByInstitution(Long institutionId);

    /**
     * 요양보호사 상세 조회
     */
    CareGiverResponseDto getCareGiverDetail(Long institutionId, Long careGiverId);

}
