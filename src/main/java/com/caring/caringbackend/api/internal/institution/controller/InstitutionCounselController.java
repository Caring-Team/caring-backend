package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCounselUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselResponseDto;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions/counsels")
@RequiredArgsConstructor
@Tag(name = "💬 Institution Counsel", description = "기관 상담 관리 API")
public class InstitutionCounselController {

    private final InstitutionCounselService institutionCounselService;

    // 기관 상담 서비스 등록
    @PostMapping
    @Operation(summary = "기관 상담 서비스 등록")
    public ApiResponse<Void> createInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @Valid @RequestBody InstitutionCounselCreateRequestDto requestDto) {
        institutionCounselService.createInstitutionCounsel(adminDetails.getId(), requestDto);
        return ApiResponse.success();
    }

    // 기관 상담 서비스 목록 조회
    @GetMapping
    @Operation(summary = "기관 상담 서비스 목록 조회")
    public ApiResponse<List<InstitutionCounselResponseDto>> getInstitutionCounsels(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails) {
        List<InstitutionCounselResponseDto> responseDto =
                institutionCounselService.getInstitutionCounsels(adminDetails.getId());
        return ApiResponse.success(responseDto);
    }


    // 기관 상담 서비스 상세 조회 -> 상담 예약 가능 시간 데이터 중요
    // 상담을 통해 세부 정보를 누를때 detail 동적 생성
    @GetMapping("/{counselId}/details")
    @Operation(summary = "상담 예약 가능 시간 조회")
    public ApiResponse<InstitutionCounselReservationDetailResponseDto> getInstitutionCounselDetail(
            @PathVariable Long counselId,
            @RequestParam("date") LocalDate date
    ) {
        InstitutionCounselReservationDetailResponseDto responseDto =
                institutionCounselService.getOrCreateCounselDetail(counselId, date);
        return ApiResponse.success(responseDto);
    }

    @GetMapping("/{counselId}")
    @Operation(summary = "상담 서비스 정보 조회")
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
    @Operation(summary = "상담 서비스 정보 수정")
    public ApiResponse<Void> updateInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId,
            @Valid @RequestBody InstitutionCounselUpdateRequestDto requestDto) {

        institutionCounselService.updateInstitutionCounsel(
                adminDetails.getId(), counselId, requestDto);

        return ApiResponse.success();
    }

    // 상담 서비스 제공 여부 변경
    @PatchMapping("/{counselId}/status")
    @Operation(summary = "상담 서비스 제공 여부 토글")
    public ApiResponse<CounselStatus> toggleInstitutionCounselStatus(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId) {
        CounselStatus currentStatus = institutionCounselService.toggleInstitutionCounselStatus(
                adminDetails.getId(), counselId);

        return ApiResponse.success(currentStatus);
    }


    // 상담 서비스 삭제 (soft delete)
    @DeleteMapping("/{counselId}")
    @Operation(summary = "상담 서비스 삭제 (soft delete)")
    public ApiResponse<Void> deleteInstitutionCounsel(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long counselId) {
        institutionCounselService.deleteCounselByCounselId(adminDetails.getId(), counselId);
        return ApiResponse.success();
    }
}
