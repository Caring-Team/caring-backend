package com.caring.caringbackend.api.internal.Member.controller;

import com.caring.caringbackend.api.internal.Member.dto.recommendation.request.RecommendRequestDto;
import com.caring.caringbackend.api.internal.Member.dto.recommendation.response.RecommendationResponseDto;
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
@RequestMapping("/api/v1/members/me/recommendations")
@Tag(name = "09. 🤖 AI Recommendation", description = "AI 추천 API | AI 기반 맞춤 기관 추천")
public class MemberRecommendationsController {

    private final RecommendationService recommendationService;

    /**
     * 추천 기관 조회
     * @param memberDetails 인증된 회원 정보
     * @param recommendRequestDto 추천 요청 DTO
     * @return 추천 기관 목록
     */
    @PostMapping
    @Operation(summary = "1. AI 기관 추천", description = "회원과 어르신 프로필을 기반으로 AI가 최적의 기관을 추천합니다.")
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
