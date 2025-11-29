package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.internal.institution.dto.request.careGiver.CareGiverCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.careGiver.CareGiverUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.CareGiverResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CareGiverService {

    /**
     * 요양보호사 등록
     */
    void registerCareGiver(Long adminId, CareGiverCreateRequestDto requestDto, MultipartFile photo);

    /**
     * 기관별 요양보호사 목록 조회
     */
    List<CareGiverResponseDto> getCareGiversByInstitution(Long adminId);

    /**
     * 요양보호사 상세 조회
     */
    CareGiverResponseDto getCareGiverDetail(Long adminId, Long careGiverId);

    /**
     * 요양보호사 정보 수정
     */
    void updateCareGiver(Long adminId, Long careGiverId, CareGiverUpdateRequestDto requestDto);

    /**
     * 요양보호사 사진 수정
     */
    void updateCareGiverPhoto(Long adminId, Long careGiverId, MultipartFile photo);

    /**
     * 요양보호사 삭제 (Soft Delete)
     */
    void deleteCareGiver(Long adminId, Long careGiverId);
}
