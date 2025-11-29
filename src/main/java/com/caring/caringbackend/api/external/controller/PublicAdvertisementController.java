package com.caring.caringbackend.api.external.controller;

import com.caring.caringbackend.api.internal.institution.dto.response.advertisement.ActiveAdvertisementDto;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.service.AdvertisementService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공개 광고 조회 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/public/advertisements")
@RequiredArgsConstructor
@Tag(name = "21. 📺 Public Advertisement", description = "공개 광고 API | 광고 조회 (인증 불필요)")
public class PublicAdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * 현재 진행중인 광고 목록 조회
     */
    @GetMapping
    @Operation(summary = "1. 현재 진행중인 광고 목록", description = "현재 진행중인 모든 광고를 조회합니다. (공개 API)")
    public ApiResponse<List<ActiveAdvertisementDto>> getActiveAdvertisements() {
        log.info("현재 진행중인 광고 목록 조회 (공개 API)");

        List<ActiveAdvertisementDto> response = advertisementService.getActiveAdvertisements();

        return ApiResponse.success(response);
    }

    /**
     * 유형별 진행중인 광고 조회
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "2. 유형별 진행중인 광고 조회", description = "특정 유형의 진행중인 광고를 조회합니다. (공개 API)")
    public ApiResponse<List<ActiveAdvertisementDto>> getActiveAdvertisementsByType(
            @Parameter(description = "광고 유형") @PathVariable AdvertisementType type
    ) {
        log.info("유형별 진행중인 광고 조회 (공개 API) - type: {}", type);

        List<ActiveAdvertisementDto> response = advertisementService.getActiveAdvertisementsByType(type);

        return ApiResponse.success(response);
    }
}

