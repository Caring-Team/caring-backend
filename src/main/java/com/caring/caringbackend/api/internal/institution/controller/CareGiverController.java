package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.CareGiverCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.CareGiverUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.CareGiverResponseDto;
import com.caring.caringbackend.domain.institution.profile.service.CareGiverService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/me/caregivers")
@Tag(name = "👩‍⚕️ CareGiver", description = "요양보호사 관리 API")
public class CareGiverController {

    private final CareGiverService careGiverService;

    /**
     * 요양보호사 등록
     */
    @PostMapping
    @Operation(summary = "요양보호사 등록", description = "해당 기관에 새로운 요양보호사를 등록합니다. (OWNER/MANAGER 권한 필요)")
    public ApiResponse<Void> registerCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @Valid @RequestBody CareGiverCreateRequestDto requestDto) {

        careGiverService.registerCareGiver(adminDetails.getId(), institutionId, requestDto);
        return ApiResponse.success();
    }

    /**
     * 요양보호사 목록 조회
     */
    @GetMapping
    @Operation(summary = "요양보호사 목록 조회", description = "해당 기관에 소속된 요양보호사들의 전체 목록을 조회합니다. (공개 API)")
    public ApiResponse<List<CareGiverResponseDto>> getCareGiversByInstitution(
            @PathVariable Long institutionId) {

        List<CareGiverResponseDto> careGivers = careGiverService.getCareGiversByInstitution(institutionId);
        return ApiResponse.success(careGivers);
    }

    /**
     * 요양보호사 상세 조회
     */
    @GetMapping("/{careGiverId}")
    @Operation(summary = "요양보호사 상세 조회", description = "요양보호사의 상세 정보를 조회합니다. (공개 API)")
    public ApiResponse<CareGiverResponseDto> getCareGiverDetail(
            @PathVariable Long institutionId,
            @PathVariable Long careGiverId) {

        CareGiverResponseDto careGiver = careGiverService.getCareGiverDetail(institutionId, careGiverId);
        return ApiResponse.success(careGiver);
    }

    /**
     * 요양보호사 정보 수정
     */
    @PutMapping("/{careGiverId}")
    @Operation(summary = "요양보호사 정보 수정", description = "요양보호사의 정보를 수정합니다. (OWNER/MANAGER 권한 필요)")
    public ApiResponse<Void> updateCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @PathVariable Long careGiverId,
            @Valid @RequestBody CareGiverUpdateRequestDto requestDto) {

        careGiverService.updateCareGiver(adminDetails.getId(), institutionId, careGiverId, requestDto);
        return ApiResponse.success();
    }

    /**
     * 요양보호사 삭제
     */
    @DeleteMapping("/{careGiverId}")
    @Operation(summary = "요양보호사 삭제", description = "요양보호사를 삭제합니다. Soft Delete로 처리됩니다. (OWNER 권한 필요)")
    public ApiResponse<Void> deleteCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long institutionId,
            @PathVariable Long careGiverId) {

        careGiverService.deleteCareGiver(adminDetails.getId(), institutionId, careGiverId);
        return ApiResponse.success();
    }
}
