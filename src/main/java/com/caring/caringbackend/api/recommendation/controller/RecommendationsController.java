package com.caring.caringbackend.api.recommendation.controller;

import com.caring.caringbackend.api.recommendation.dto.request.RecommendRequestDto;
import com.caring.caringbackend.api.recommendation.dto.response.RecommendationResponseDto;
import com.caring.caringbackend.domain.recommendation.service.RecommendationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class RecommendationsController {

    private final RecommendationService recommendationService;

    /**
     * 추천 기관 조회
     * @param memberDetails 인증된 회원 정보
     *
     */
    @PostMapping
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
