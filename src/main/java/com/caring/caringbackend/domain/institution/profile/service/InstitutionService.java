package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionProfileResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InstitutionService {

    /**
     * 기관 등록
     * @param adminId 관리자 ID
     * @param requestDto 기관 생성 요청 DTO
     */
    void registerInstitution(Long adminId, InstitutionCreateRequestDto requestDto, MultipartFile file);

    /**
     * 기관 목록 조회 (페이징, 검색, 필터링)
     *
     * @param pageable 페이징 및 정렬 정보
     * @param filter 검색 필터
     * @return 기관 프로필 응답 DTO 페이지
     */
    Page<InstitutionProfileResponseDto> getInstitutions(Pageable pageable, InstitutionSearchFilter filter);

    /**
     * 기관 상세 조회
     * @param institutionId 기관 ID
     */
    InstitutionDetailResponseDto getInstitutionDetail(Long institutionId);

    /**
     * 기관 정보 수정
     * @param adminId 관리자 ID
     * @param requestDto    기관 수정 요청 DTO
     */
    void updateInstitution(Long adminId, InstitutionUpdateRequestDto requestDto, MultipartFile mainImage);

    /**
     * 입소 가능 여부 변경
     * @param adminId 관리자 ID
     * @param isAdmissionAvailable  입소 가능 여부
     */
    void changeAdmissionAvailability(Long adminId, Boolean isAdmissionAvailable);

    /**
     * 기관 승인 처리
     *
     * @param institutionId 기관 ID
     */
    void approveInstitution(Long institutionId);

    /**
     * 기관 삭제 (Soft Delete)
     *
     * @param adminId 관리자 ID
     */
    void deleteInstitution(Long adminId);

    /**
     * 기관 태그 설정
     *
     * @param adminId 관리자 ID
     * @param tagIds 태그 ID 목록
     */
    void setInstitutionTags(Long adminId, List<Long> tagIds);

    InstitutionDetailResponseDto getMyInstitution(Long adminId);
}
