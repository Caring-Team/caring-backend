package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.institution.dto.request.CareGiverCreateRequestDto;
import com.caring.caringbackend.domain.institution.profile.service.CareGiverService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/caregivers")
@Tag(name = "️ 👩‍⚕️ CareGiver", description = "요양사 관리 API")
public class CareGiverController {

    private final CareGiverService careGiverService;

    // 요양 보호사 등록
    @PostMapping("{institutionId}/register")
    @Operation(summary = "요양 보호사 등록", description = "해당 기관에 새로운 요양 보호사를 등록합니다.")
    public ApiResponse<Void> registerCareGiver(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @RequestParam Long institutionId,
            @RequestBody CareGiverCreateRequestDto requestDto) {

        careGiverService.registerCareGiver(adminDetails.getId(), institutionId, requestDto);
        return ApiResponse.success();
    }


    // 해당 기관의 요양 보호사 목록 조회


    // 요양 보호사 상세 조회


    // 요양 보호사 정보 수정


    // 요양 보호사 활성 상태 변경


    // 요양 보호사 삭제
}
