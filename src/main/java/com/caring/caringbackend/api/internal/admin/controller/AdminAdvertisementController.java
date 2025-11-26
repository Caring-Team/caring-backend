package com.caring.caringbackend.api.internal.admin.controller;

import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementRequestDetailDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.AdvertisementSummaryDto;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.service.AdvertisementService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 광고 심사 및 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/advertisements")
@RequiredArgsConstructor
@Tag(name = "21. Admin Advertisement", description = "관리자 광고 관리 API | 광고 심사/승인/거절")
public class AdminAdvertisementController {

    private final AdvertisementService advertisementService;

    // ==================== 신청 심사 ====================

    /**
     * 전체 신청 목록 조회 (관리자용)
     */
    @GetMapping("/requests")
    @Operation(summary = "1. 전체 광고 신청 목록 조회", description = "관리자가 전체 광고 신청 목록을 조회합니다.")
    public ApiResponse<Page<AdvertisementSummaryDto>> getAllRequests(
            @Parameter(description = "신청 상태 필터") @RequestParam(required = false) AdvertisementStatus status,
            @Parameter(description = "광고 유형 필터") @RequestParam(required = false) AdvertisementType type,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("전체 신청 목록 조회 (관리자) - status: {}, type: {}", status, type);

        Page<AdvertisementSummaryDto> response = advertisementService.getAllRequests(
                status,
                type,
                pageable
        );

        return ApiResponse.success(response);
    }

    /**
     * 승인 대기 신청 목록 조회
     */
    @GetMapping("/requests/pending")
    @Operation(summary = "2. 승인 대기 신청 목록", description = "승인 대기 중인 광고 신청 목록을 조회합니다.")
    public ApiResponse<Page<AdvertisementSummaryDto>> getPendingRequests(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<AdvertisementSummaryDto> response = advertisementService.getPendingRequests(pageable);

        return ApiResponse.success(response);
    }

    /**
     * 신청 상세 조회 (관리자용)
     */
    @GetMapping("/requests/{requestId}")
    @Operation(summary = "3. 광고 신청 상세 조회 (관리자)", description = "관리자가 특정 광고 신청의 상세 정보를 조회합니다.")
    public ApiResponse<AdvertisementRequestDetailDto> getRequestDetail(
            @PathVariable Long requestId
    ) {
        log.info("신청 상세 조회 (관리자) - requestId: {}", requestId);

        AdvertisementRequestDetailDto response = advertisementService.getRequestDetailForAdmin(requestId);

        return ApiResponse.success(response);
    }

    /**
     * 광고 신청 승인 (Advertisement 생성)
     */
    @PatchMapping("/requests/{requestId}/approve")
    @Operation(summary = "4. 광고 신청 승인", description = "관리자가 광고 신청을 승인하고 광고를 생성합니다.")
    public ApiResponse<AdvertisementResponseDto> approveRequest(
            @PathVariable Long requestId,
            @Parameter(description = "승인 메모") @RequestParam(required = false) String memo
    ) {
        log.info("신청 승인 요청 - requestId: {}", requestId);

        AdvertisementResponseDto response = advertisementService.approveRequest(requestId, memo);

        return ApiResponse.success("광고 신청이 승인되었습니다.", response);
    }

    /**
     * 광고 신청 거절
     */
    @PatchMapping("/requests/{requestId}/reject")
    @Operation(summary = "5. 광고 신청 거절", description = "관리자가 광고 신청을 거절합니다. 거절 사유 필수.")
    public ApiResponse<AdvertisementRequestDetailDto> rejectRequest(
            @PathVariable Long requestId,
            @Parameter(description = "거절 사유", required = true) @RequestParam @NotBlank String rejectionReason
    ) {
        log.info("신청 거절 요청 - requestId: {}", requestId);

        AdvertisementRequestDetailDto response = advertisementService.rejectRequest(requestId, rejectionReason);

        return ApiResponse.success("광고 신청이 거절되었습니다.", response);
    }

    // ==================== 광고 관리 ====================

    /**
     * 전체 광고 목록 조회
     */
    @GetMapping
    @Operation(summary = "6. 전체 광고 목록 조회", description = "관리자가 승인된 전체 광고 목록을 조회합니다.")
    public ApiResponse<Page<AdvertisementSummaryDto>> getAllAdvertisements(
            @Parameter(description = "광고 상태 필터") @RequestParam(required = false) AdvertisementStatus status,
            @Parameter(description = "광고 유형 필터") @RequestParam(required = false) AdvertisementType type,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("전체 광고 목록 조회 (관리자) - status: {}, type: {}", status, type);

        Page<AdvertisementSummaryDto> response = advertisementService.getAllAdvertisements(
                status,
                type,
                pageable
        );

        return ApiResponse.success(response);
    }

    /**
     * 광고 상세 조회 (관리자용)
     */
    @GetMapping("/{adId}")
    @Operation(summary = "7. 광고 상세 조회 (관리자)", description = "관리자가 특정 광고의 상세 정보를 조회합니다.")
    public ApiResponse<AdvertisementResponseDto> getAdvertisementDetail(
            @PathVariable Long adId
    ) {
        log.info("광고 상세 조회 (관리자) - adId: {}", adId);

        AdvertisementResponseDto response = advertisementService.getAdvertisementDetailForAdmin(adId);

        return ApiResponse.success(response);
    }

    /**
     * 광고 강제 종료
     */
    @PatchMapping("/{advertisementId}/force-end")
    @Operation(summary = "8. 광고 강제 종료", description = "관리자가 진행중인 광고를 강제로 종료합니다.")
    public ApiResponse<AdvertisementResponseDto> forceEndAdvertisement(
            @PathVariable Long advertisementId,
            @Parameter(description = "종료 사유", required = true) @RequestParam @NotBlank String reason
    ) {
        log.info("광고 강제 종료 요청 - adId: {}", advertisementId);

        AdvertisementResponseDto response = advertisementService.forceEndAdvertisement(advertisementId, reason);

        return ApiResponse.success("광고가 종료되었습니다.", response);
    }
}



