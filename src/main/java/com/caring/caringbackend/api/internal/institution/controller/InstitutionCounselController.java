package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.counsel.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.counsel.InstitutionCounselResponseDto;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.service.InstitutionCounselService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions/me/counsels")
@RequiredArgsConstructor
@Tag(name = "12. 💬 Institution Counsel", description = "기관 상담 서비스 관리 API | 상담 서비스 등록/수정/삭제, 시간 관리")
public class InstitutionCounselController {

    private final InstitutionCounselService institutionCounselService;

    // 기관 상담 서비스 등록
    @PostMapping
    @Operation(summary = "1. 내 기관 상담 서비스 등록", description = "내 기관의 상담 서비스를 등록합니다.")
    public ApiResponse<Void> createInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @Valid @RequestBody InstitutionCounselCreateRequestDto requestDto
    ) {
        institutionCounselService.createInstitutionCounsel(adminDetails.getId(), requestDto);
        return ApiResponse.success();
    }

    // 기관 상담 서비스 목록 조회
    @GetMapping
    @Operation(summary = "2. 내 기관 상담 서비스 목록 조회", description = "내 기관의 상담 서비스 목록을 조회합니다.")
    public ApiResponse<List<InstitutionCounselResponseDto>> getInstitutionCounsels(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        List<InstitutionCounselResponseDto> responseDto = institutionCounselService.getInstitutionCounsels(adminDetails.getId());
        return ApiResponse.success(responseDto);
    }

    @GetMapping("/{counselId}")
    @Operation(summary = "3. 내 상담 서비스 정보 조회", description = "내 기관의 상담 서비스 상세 정보를 조회합니다.")
    public ApiResponse<InstitutionCounselDetailResponseDto> getInstitutionCounselDetail(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId
    ) {
        InstitutionCounselDetailResponseDto responseDto =
                institutionCounselService.getCounselDetail(adminDetails.getId(), counselId);
        return ApiResponse.success(responseDto);
    }

    // 상담 서비스 정보 수정
    @PatchMapping("/{counselId}")
    @Operation(summary = "4. 내 상담 서비스 정보 수정", description = "내 기관의 상담 서비스 정보를 수정합니다.")
    public ApiResponse<Void> updateInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId,
            @Valid @RequestBody InstitutionCounselUpdateRequestDto requestDto) {

        institutionCounselService.updateInstitutionCounsel(adminDetails.getId(), counselId, requestDto);

        return ApiResponse.success();
    }

    // 상담 서비스 제공 여부 변경
    @PatchMapping("/{counselId}/status")
    @Operation(summary = "5. 상담 서비스 제공 여부 토글", description = "내 기관의 상담 서비스 제공 여부를 토글합니다.")
    public ApiResponse<CounselStatus> toggleInstitutionCounselStatus(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId) {
        CounselStatus currentStatus = institutionCounselService.toggleInstitutionCounselStatus(
                adminDetails.getId(), counselId);

        return ApiResponse.success(currentStatus);
    }


    // 상담 서비스 삭제 (soft delete)
    @DeleteMapping("/{counselId}")
    @Operation(summary = "6. 상담 서비스 삭제 (soft delete)", description = "내 기관의 상담 서비스를 삭제합니다. (soft delete)")
    public ApiResponse<Void> deleteInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId) {
        institutionCounselService.deleteCounselByCounselId(adminDetails.getId(), counselId);
        return ApiResponse.success();
    }
}
