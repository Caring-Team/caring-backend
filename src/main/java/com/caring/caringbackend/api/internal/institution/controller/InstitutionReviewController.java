package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.request.review.InstitutionReviewSearchFilter;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import com.caring.caringbackend.domain.review.service.InstitutionReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/me/reviews")
@Tag(name = "17. 🏥 Institution Review", description = "기관 리뷰 관리 API | 기관 리뷰 조회 및 관리")
public class InstitutionReviewController {
    private final InstitutionReviewService institutionReviewService;

    /**
     * 내 기관의 리뷰 목록 조회
     *
     * @param adminDetails 인증된 기관 관리자 정보
     * @param searchFilter 검색 필터 (daysAgo: null이면 전체 조회, 값이 있으면 해당 일수만큼 과거 리뷰 조회)
     * @return 내 기관의 리뷰 목록
     */
    @GetMapping
    @Operation(
            summary = "1. 내 기관 리뷰 목록 조회",
            description = "내 기관의 리뷰 목록을 조회합니다. daysAgo 파라미터가 null이면 전체 리뷰를 조회하고, 값이 있으면 해당 일수만큼의 최근 리뷰만 조회합니다."
    )
    public ApiResponse<InstitutionReviewsResponseDto> getInstitutionReviews(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @ParameterObject InstitutionReviewSearchFilter searchFilter
            ) {
        InstitutionReviewsResponseDto myInstitutionReviews = institutionReviewService.getMyInstitutionReviews(
                adminDetails.getId(),
                searchFilter
        );
        return ApiResponse.success(myInstitutionReviews);
    }
}
