package com.caring.caringbackend.api.internal.user.controller;

import com.caring.caringbackend.api.internal.user.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.internal.user.dto.review.request.ReviewReportRequest;
import com.caring.caringbackend.api.internal.user.dto.review.request.ReviewUpdateRequest;
import com.caring.caringbackend.api.internal.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.internal.user.dto.review.response.ReviewResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ⭐ 리뷰(Review) 관리 Controller
 *
 * 리뷰 CRUD 기능을 제공하는 REST API 엔드포인트입니다.
 *
 * @author 윤다인
 * @since 2025-11-05
 */
@RestController
@RequestMapping("/api/v1/members/me/reviews")
@RequiredArgsConstructor
@Tag(name = "⭐ Review", description = "리뷰 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class MemberReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     */
    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "1. 리뷰 작성", description = "완료된 예약에 대한 리뷰를 작성합니다. (이미지 최대 5개)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestPart("request") ReviewCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ReviewResponse review = reviewService.createReview(memberDetails.getId(), request, images);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰 작성 성공", review));
    }

    /**
     * 내가 작성한 리뷰 목록 조회
     */
    @GetMapping
    @Operation(summary = "2. 내가 작성한 리뷰 목록 조회", description = "인증된 사용자가 작성한 리뷰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getMyReviews(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ReviewListResponse reviews = reviewService.getMyReviews(memberDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("내 리뷰 목록 조회 성공", reviews));
    }

    /**
     * 내 리뷰 상세 조회
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "3. 내 리뷰 상세 조회", description = "내 리뷰의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ReviewResponse>> getMyReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        ReviewResponse review = reviewService.getMyReview(reviewId, memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("리뷰 조회 성공", review));
    }

//    /**
//     * 리뷰 상세 조회
//     */
//    @GetMapping("/{reviewId}")
//    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다. (공개)")
//    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
//            @PathVariable Long reviewId) {
//
//        ReviewResponse review = reviewService.getReview(reviewId);
//        return ResponseEntity.ok(ApiResponse.success("리뷰 조회 성공", review));
//    }

    /**
     * 리뷰 수정
     */
    @PutMapping(value = "/{reviewId}", consumes = {"multipart/form-data"})
    @Operation(summary = "4. 내 리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다. (작성 후 30일 이내만 수정 가능, 이미지 최대 5개)")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long reviewId,
            @Valid @RequestPart("request") ReviewUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ReviewResponse review = reviewService.updateReview(memberDetails.getId(), reviewId, request, images);
        return ResponseEntity.ok(ApiResponse.success("리뷰 수정 성공", review));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "5. 내 리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다. (Soft Delete)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(memberDetails.getId(), reviewId);
        return ResponseEntity.ok(ApiResponse.success("리뷰 삭제 성공", null));
    }

    /**
     * 리뷰 신고
     */
    @PostMapping("/{reviewId}/report")
    @Operation(summary = "6. 리뷰 신고", description = "부적절한 리뷰를 신고합니다. (본인 리뷰는 신고 불가, 중복 신고 방지)")
    public ResponseEntity<ApiResponse<Void>> reportReview(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReportRequest request) {

        reviewService.reportReview(memberDetails.getId(), reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰 신고 완료", null));
    }
}


