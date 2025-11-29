package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import com.caring.caringbackend.domain.review.service.InstitutionReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return 내 기관의 리뷰 목록
     */
    @GetMapping
    public ApiResponse<InstitutionReviewsResponseDto> getInstitutionReviews(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        InstitutionReviewsResponseDto myInstitutionReviews = institutionReviewService.getMyInstitutionReviews(adminDetails.getId());
        return ApiResponse.success(myInstitutionReviews);
    }
}
