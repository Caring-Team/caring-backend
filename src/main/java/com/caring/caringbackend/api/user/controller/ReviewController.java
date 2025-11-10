package com.caring.caringbackend.api.user.controller;

import com.caring.caringbackend.api.user.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.user.dto.review.response.ReviewResponse;
import com.caring.caringbackend.domain.review.service.ReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ⭐ 리뷰(Review) 관리 Controller
 *
 * 리뷰 CRUD 기능을 제공하는 REST API 엔드포인트입니다.
 *
 * @author 윤다인
 * @since 2025-11-05
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "⭐ Review", description = "리뷰 관리 API")
@SecurityRequirement(name = "BearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     */
    @PostMapping
    @Operation(summary = "리뷰 작성", description = "완료된 예약에 대한 리뷰를 작성합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewResponse review = reviewService.createReview(memberDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰 작성 성공", review));
    }

    /**
     * 내가 작성한 리뷰 목록 조회
     */
    @GetMapping("/my")
    @Operation(summary = "내가 작성한 리뷰 목록", description = "인증된 사용자가 작성한 리뷰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getMyReviews(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ReviewListResponse reviews = reviewService.getMyReviews(memberDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("내 리뷰 목록 조회 성공", reviews));
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다. (공개)")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable Long reviewId) {

        ReviewResponse review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰 조회 성공", review));
    }

    /**
     * 특정 기관의 리뷰 목록 조회 (공개)
     */
    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "기관 리뷰 목록 조회", description = "특정 기관에 등록된 리뷰 목록을 조회합니다. (공개)")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getInstitutionReviews(
            @PathVariable Long institutionId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ReviewListResponse reviews = reviewService.getInstitutionReviews(institutionId, pageable);
        return ResponseEntity.ok(ApiResponse.success("기관 리뷰 목록 조회 성공", reviews));
    }
}


