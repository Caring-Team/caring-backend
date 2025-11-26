package com.caring.caringbackend.api.internal.admin.controller;

import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/institutions")
@RequiredArgsConstructor
@Tag(name = "Admin Institution", description = "관리자 기관 관리 API")
public class AdminInstitutionController {

    private InstitutionService institutionService;

    /**
     * 기관 승인 처리 (관리자 전용)
     */
    @PatchMapping("/{institutionId}/approval")
    @Operation(summary = "기관 승인", description = "관리자가 기관 등록 요청을 승인합니다.")
    public ApiResponse<Void> approveInstitution(
            @PathVariable Long institutionId
    ) {
        // TODO: 관리자 권한 체크
        institutionService.approveInstitution(institutionId);
        return ApiResponse.success(null);
    }
}
