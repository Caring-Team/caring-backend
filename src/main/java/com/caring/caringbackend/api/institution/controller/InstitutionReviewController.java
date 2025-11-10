package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.domain.review.service.ReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🏥 기관 리뷰 조회 Controller
 *
 * 기관의 리뷰 목록을 조회하는 REST API 엔드포인트입니다.
 * 공개 API로 인증 없이 접근 가능합니다.
 *
 * @author 윤다인
 * @since 2025-11-05
 */
@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
@Tag(name = "🏥 Institution Review", description = "기관 리뷰 조회 API")
public class InstitutionReviewController {

    private final ReviewService reviewService;

    /**
     * 기관의 리뷰 목록 조회 (공개)
     */
    @GetMapping("/{institutionId}/reviews")
    @Operation(summary = "기관 리뷰 목록 조회", description = "특정 기관의 리뷰 목록을 조회합니다. (공개, 삭제되지 않은 리뷰만)")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getInstitutionReviews(
            @PathVariable Long institutionId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ReviewListResponse reviews = reviewService.getInstitutionReviews(institutionId, pageable);
        return ResponseEntity.ok(ApiResponse.success("기관 리뷰 목록 조회 성공", reviews));
    }
}
