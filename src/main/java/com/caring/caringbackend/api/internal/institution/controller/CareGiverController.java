package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.careGiver.CareGiverCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.careGiver.CareGiverUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.CareGiverResponseDto;
import com.caring.caringbackend.domain.institution.profile.service.CareGiverService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/me/caregivers")
@Tag(name = "11. 👩‍⚕️ Institution CareGiver", description = "기관 요양보호사 관리 API | 요양보호사 등록/수정/삭제")
public class CareGiverController {

    private final CareGiverService careGiverService;

    /**
     * 요양보호사 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "1. 요양보호사 등록", description = "해당 기관에 새로운 요양보호사를 등록합니다. (OWNER/MANAGER 권한 필요)")
    public ApiResponse<Void> registerCareGiver(
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @Valid @RequestPart(value = "data") CareGiverCreateRequestDto requestDto,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        careGiverService.registerCareGiver(adminDetails.getId(), requestDto, photo);
        return ApiResponse.success();
    }

    /**
     * 요양보호사 목록 조회
     */
    @GetMapping
    @Operation(summary = "2. 내 기관 요양보호사 목록 조회", description = "해당 기관에 소속된 요양보호사들의 전체 목록을 조회합니다. (공개 API)")
    public ApiResponse<List<CareGiverResponseDto>> getCareGiversByInstitution(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
            ) {

        List<CareGiverResponseDto> careGivers = careGiverService.getCareGiversByInstitution(adminDetails.getId());
        return ApiResponse.success(careGivers);
    }

    /**
     * 요양보호사 상세 조회
     */
    @GetMapping("/{careGiverId}")
    @Operation(summary = "3. 내 기관 요양보호사 상세 조회", description = "요양보호사의 상세 정보를 조회합니다. (공개 API)")
    public ApiResponse<CareGiverResponseDto> getCareGiverDetail(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long careGiverId) {

        CareGiverResponseDto careGiver = careGiverService.getCareGiverDetail(adminDetails.getId(), careGiverId);
        return ApiResponse.success(careGiver);
    }

    /**
     * 요양보호사 정보 수정
     */
    @PutMapping("/{careGiverId}")
    @Operation(summary = "4. 내 기관 요양보호사 정보 수정", description = "요양보호사의 정보를 수정합니다. (OWNER/MANAGER 권한 필요)")
    public ApiResponse<Void> updateCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long careGiverId,
            @Valid @RequestBody CareGiverUpdateRequestDto requestDto) {

        careGiverService.updateCareGiver(adminDetails.getId(), careGiverId, requestDto);
        return ApiResponse.success();
    }

    /**
     * 요양보호사 사진 수정
     */
    @PatchMapping(value = "/{careGiverId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "4-1. 내 기관 요양보호사 사진 수정", description = "요양보호사의 사진을 수정합니다. (OWNER/MANAGER 권한 필요)")
    public ApiResponse<Void> updateCareGiverPhoto(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long careGiverId,
            @RequestPart("photo") MultipartFile photo) {

        careGiverService.updateCareGiverPhoto(adminDetails.getId(), careGiverId, photo);
        return ApiResponse.success();
    }

    /**
     * 요양보호사 삭제
     */
    @DeleteMapping("/{careGiverId}")
    @Operation(summary = "5. 내 기관 요양보호사 삭제", description = "요양보호사를 삭제합니다. Soft Delete로 처리됩니다. (OWNER 권한 필요)")
    public ApiResponse<Void> deleteCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @PathVariable Long careGiverId) {

        careGiverService.deleteCareGiver(adminDetails.getId(), careGiverId);
        return ApiResponse.success();
    }
}
