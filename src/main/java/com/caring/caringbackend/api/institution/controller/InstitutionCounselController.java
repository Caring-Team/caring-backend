package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.service.InstitutionCounselDetailService;
import com.caring.caringbackend.domain.institution.counsel.service.InstitutionCounselService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions/{institutionId}/counsels")
@RequiredArgsConstructor
@Tag(name = "💬 Institution Counsel", description = "기관 상담 관리 API")
public class InstitutionCounselController {

    private final InstitutionCounselService institutionCounselService;
    private final InstitutionCounselDetailService institutionCounselDetailService;

    // 기관 상담 서비스 등록
    @PostMapping
    public ApiResponse<Void> createInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @Valid @RequestBody InstitutionCounselCreateRequestDto requestDto) {
        institutionCounselService.createInstitutionCounsel(adminDetails.getId(), institutionId, requestDto);
        return ApiResponse.success();
    }

    // 기관 상담 서비스 목록 조회
    @GetMapping
    public ApiResponse<List<InstitutionCounselResponseDto>> getInstitutionCounsels(
            @PathVariable Long institutionId) {
        List<InstitutionCounselResponseDto> responseDto = institutionCounselService.getInstitutionCounsels(institutionId);
        return ApiResponse.success(responseDto);
    }


    // 기관 상담 서비스 상세 조회 -> 상담 예약 가능 시간 데이터 중요


    // 상담 서비스 정보 수정


    // 상담 서비스 제공 여부 변경
    @PatchMapping("/{counselId}/status")
    public ApiResponse<CounselStatus> toggleInstitutionCounselStatus(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @PathVariable Long counselId) {
        CounselStatus currentStatus = institutionCounselService.toggleInstitutionCounselStatus(
                adminDetails.getId(), institutionId, counselId);

        return ApiResponse.success(currentStatus);
    }


    // 상담 서비스 삭제 (soft delete)
    @DeleteMapping("/{counselId}")
    public ApiResponse<Void> deleteInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @PathVariable Long counselId) {
        institutionCounselService.deleteCounselByCouncelId(adminDetails.getId(), institutionId, counselId);
        return ApiResponse.success();
    }
}
