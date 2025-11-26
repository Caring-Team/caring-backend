package com.caring.caringbackend.api.internal.recommendation.controller;

import com.caring.caringbackend.api.internal.recommendation.dto.request.RecommendRequestDto;
import com.caring.caringbackend.api.internal.recommendation.dto.response.RecommendationResponseDto;
import com.caring.caringbackend.domain.recommendation.service.RecommendationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
@Tag(name = "🤖 AI Recommendation", description = "AI 기관 추천 API")
public class RecommendationsController {

    private final RecommendationService recommendationService;

    /**
     * 추천 기관 조회
     * @param memberDetails 인증된 회원 정보
     * @param recommendRequestDto 추천 요청 DTO
     * @return 추천 기관 목록
     */
    @PostMapping
    @Operation(summary = "1. AI 기관 추천 받기", description = "어르신 프로필을 기반으로 AI가 최적의 기관을 추천합니다.")
    public ApiResponse<RecommendationResponseDto> getRecommendations(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody RecommendRequestDto recommendRequestDto
            ) {
        RecommendationResponseDto recommendationResponseDto = recommendationService.recommendInstitutions(
                memberDetails.getId(),
                recommendRequestDto
        );
        return ApiResponse.success(recommendationResponseDto);
    }
}
