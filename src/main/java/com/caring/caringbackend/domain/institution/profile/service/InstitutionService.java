package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;

public interface InstitutionService {

    /**
     * 기관 등록
     * @param requestDto 기관 생성 요청 DTO
     */
    void registerInstitution(InstitutionCreateRequestDto requestDto);


    /**
     * 기관 목록 조회
     */
    void getInstitutions();


    /**
     * 기관 정보 수정
     * @param requestDto 기관 수정 요청 DTO
     */
    void updateInstitution(InstitutionUpdateRequestDto requestDto);
}
