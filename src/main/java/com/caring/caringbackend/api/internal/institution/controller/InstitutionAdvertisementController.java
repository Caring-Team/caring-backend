package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.advertisement.AdvertisementCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementRequestDetailDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementRequestResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementSummaryDto;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.service.AdvertisementService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 기관 광고 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/institutions/me/advertisements")
@RequiredArgsConstructor
@Tag(name = "13. 📺 Institution Advertisement", description = "기관 광고 관리 API | 광고 신청/조회/수정")
public class InstitutionAdvertisementController {

    private final AdvertisementService advertisementService;

    // ==================== 광고 신청 관리 ====================

    /**
     * 광고 신청
     */
    @PostMapping("/requests")
    @Operation(summary = "1. 광고 신청", description = "기관이 광고를 신청합니다. OWNER 권한 필요.")
    public ApiResponse<AdvertisementRequestResponseDto> createAdvertisementRequest(
            @Valid @RequestBody AdvertisementCreateRequestDto requestDto,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        AdvertisementRequestResponseDto response = advertisementService.createAdvertisementRequest(
                requestDto,
                adminDetails.getId()
        );
        return ApiResponse.success("광고 신청이 완료되었습니다.", response);
    }

    /**
     * 내 기관 광고 신청 목록 조회
     */
    @GetMapping("/requests")
    @Operation(summary = "2. 광고 신청 목록 조회", description = "기관의 광고 신청 목록을 조회합니다.")
    public ApiResponse<List<AdvertisementSummaryDto>> getInstitutionRequests(
            @Parameter(description = "신청 상태 필터") @RequestParam(required = false) AdvertisementStatus status,
            @Parameter(description = "광고 유형 필터") @RequestParam(required = false) AdvertisementType type,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        List<AdvertisementSummaryDto> response = advertisementService.getInstitutionRequests(
                status,
                type,
                adminDetails.getId()
        );

        return ApiResponse.success(response);
    }

    /**
     * 광고 신청 상세 조회
     */
    @GetMapping("/requests/{requestId}")
    @Operation(summary = "3. 광고 신청 상세 조회", description = "특정 광고 신청의 상세 정보를 조회합니다.")
    public ApiResponse<AdvertisementRequestDetailDto> getRequestDetail(
            @PathVariable Long requestId,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        AdvertisementRequestDetailDto response = advertisementService.getRequestDetail(
                requestId,
                adminDetails.getId()
        );

        return ApiResponse.success(response);
    }

    /**
     * 광고 신청 취소
     */
    @DeleteMapping("/requests/{requestId}")
    @Operation(summary = "4. 광고 신청 취소", description = "승인 대기 중인 광고 신청을 취소합니다. OWNER 권한 필요.")
    public ApiResponse<Void> cancelRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        advertisementService.cancelRequest(requestId, adminDetails.getId());
        return ApiResponse.success("광고 신청이 취소되었습니다.", null);
    }

    // ==================== 승인된 광고 관리 ====================

    /**
     * 내 기관 승인된 광고 목록 조회
     */
    @GetMapping
    @Operation(summary = "5. 승인된 광고 목록 조회", description = "기관의 승인된 광고 목록을 조회합니다.")
    public ApiResponse<List<AdvertisementSummaryDto>> getInstitutionAdvertisements(
            @Parameter(description = "광고 상태 필터")
            @RequestParam(required = false) AdvertisementStatus status,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        List<AdvertisementSummaryDto> response = advertisementService.getInstitutionAdvertisements(
                status,
                adminDetails.getId()
        );

        return ApiResponse.success(response);
    }

    /**
     * 광고 상세 조회
     */
    @GetMapping("/{advertisementId}")
    @Operation(summary = "6. 광고 상세 조회", description = "승인된 광고의 상세 정보를 조회합니다.")
    public ApiResponse<AdvertisementResponseDto> getAdvertisementDetail(
            @PathVariable Long advertisementId,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        AdvertisementResponseDto response = advertisementService.getAdvertisementDetail(
                advertisementId,
                adminDetails.getId()
        );

        return ApiResponse.success(response);
    }

    /**
     * 광고 취소
     */
    @PatchMapping("/{advertisementId}/cancel")
    @Operation(summary = "7. 광고 취소", description = "승인된 광고를 취소합니다. PENDING 상태만 가능. OWNER 권한 필요.")
    public ApiResponse<AdvertisementResponseDto> cancelAdvertisement(
            @PathVariable Long advertisementId,
            @Parameter(description = "취소 사유") @RequestParam(required = false) String cancelReason,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        AdvertisementResponseDto response = advertisementService.cancelAdvertisement(
                advertisementId,
                cancelReason,
                adminDetails.getId()
        );

        return ApiResponse.success("광고가 취소되었습니다.", response);
    }
}



