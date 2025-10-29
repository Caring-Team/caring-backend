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
     * 기관 상세 조회
     * @param institutionId 기관 ID
     */
    InstitutionDetailResponseDto getInstitutionDetail(Long institutionId);

    /**
     * 기관 정보 수정
     * @param institutionId 기관 ID
     * @param requestDto    기관 수정 요청 DTO
     */
    void updateInstitution(Long institutionId, InstitutionUpdateRequestDto requestDto);

    /**
     * 입소 가능 여부 변경
     * @param institutionId         기관 ID
     * @param isAdmissionAvailable  입소 가능 여부
     */
    void changeAdmissionAvailability(Long institutionId, Boolean isAdmissionAvailable);

    /**
     * 기관 승인 처리
     *
     * @param institutionId 기관 ID
     */
    void approveInstitution(Long institutionId);

    /**
     * 기관 삭제 (Soft Delete)
     *
     * @param institutionId 기관 ID
     */
    void deleteInstitution(Long institutionId);
}
