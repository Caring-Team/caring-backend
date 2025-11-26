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
@Tag(name = "20. Admin Institution", description = "관리자 기관 관리 API | 기관 승인/거절/조회")
public class AdminInstitutionController {

    private final InstitutionService institutionService;

    /**
     * 기관 승인 처리 (관리자 전용)
     */
    @PatchMapping("/{institutionId}/approval")
    @Operation(summary = "1. 기관 승인", description = "관리자가 기관 등록 요청을 승인합니다.")
    public ApiResponse<Void> approveInstitution(
            @PathVariable Long institutionId
    ) {
        // TODO: 관리자 권한 체크
        institutionService.approveInstitution(institutionId);
        return ApiResponse.success(null);
    }

    // 목록 조회


    // 상세 조회


    // 등록 거절


    // 기관 삭제
}
