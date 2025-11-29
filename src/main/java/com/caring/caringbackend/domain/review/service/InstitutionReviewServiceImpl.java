package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewResponse;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.entity.FileCategory;
import com.caring.caringbackend.domain.file.entity.ReferenceType;
import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import com.caring.caringbackend.domain.tag.entity.ReviewTagMapping;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.repository.ReviewTagMappingRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstitutionReviewServiceImpl implements InstitutionReviewService {

    private final FileService fileService;
    private final ReviewRepository reviewRepository;
    private final ReviewTagMappingRepository reviewTagMappingRepository;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final int RECENT_REVIEW_DAYS = 7;

    /**
     * 기관의 리뷰 목록 조회 (공개)
     *
     * @param institutionId 기관 ID
     * @param pageable      페이징 정보 (정렬: createdAt, rating 지원)
     * @return 리뷰 목록 응답
     *
     * @author 윤다인
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewListResponse getInstitutionReviews(Long institutionId, Pageable pageable) {
        // 1. 리뷰 목록 조회 (삭제되지 않은 리뷰만)
        // 기관 존재 여부는 리뷰 조회 시 자동으로 확인됨 (없으면 빈 목록 반환)
        Page<Review> reviewPage = reviewRepository.findByInstitutionIdAndDeletedFalseAndReportedFalse(
                institutionId, pageable);

        // 2. DTO 변환 (태그 및 이미지 포함)
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(review -> {
                    List<Tag> tags = reviewTagMappingRepository.findByReviewId(review.getId()).stream()
                            .map(ReviewTagMapping::getTag)
                            .collect(Collectors.toList());
                    List<String> imageUrls = getReviewImageUrls(review.getId());
                    return ReviewResponse.fromWithTagsAndImages(review, tags, imageUrls);
                })
                .collect(Collectors.toList());

        // 3. 응답 반환
        return ReviewListResponse.of(reviewResponses, reviewPage);
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionReviewsResponseDto getInstitutionDetailReviews(Long institutionId) {
        // 리뷰에 reservation, member, institution를 한번에 다 가져온다.
        List<Review> reviews = reviewRepository.findByIdWithFetches(institutionId);
        initializeLazyCollection(reviews);
        List<InstitutionReviewResponseDto> reviewResponses = reviews.stream()
                .map(InstitutionReviewResponseDto::from)
                .toList();

        return InstitutionReviewsResponseDto.of(reviewResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionReviewsResponseDto getMyInstitutionReviews(Long adminId) {
        InstitutionAdmin admin = institutionAdminRepository.findByIdWithInstitution(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        Long institutionId = admin.getInstitution().getId();
        List<Review> reviews = reviewRepository.findByIdWithFetches(institutionId);
        initializeLazyCollection(reviews);

        List<InstitutionReviewResponseDto> reviewResponses = reviews.stream()
                .map(InstitutionReviewResponseDto::from)
                .toList();
        return InstitutionReviewsResponseDto.of(reviewResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRecentReviewCount(Long institutionId) {
        LocalDateTime from = LocalDateTime.now().minusDays(RECENT_REVIEW_DAYS);
        return reviewRepository.countRecentReviews(institutionId, from)
                .getRecentReviewCount();
    }

    // ================= private methods =================

    /**
     * 리뷰 이미지 URL 조회 헬퍼 메서드
     *
     * @param reviewId 리뷰 ID
     * @return 이미지 URL 목록
     */
    private List<String> getReviewImageUrls(Long reviewId) {
        List<File> files = fileService.getFilesByReferenceAndCategory(
                reviewId,
                ReferenceType.REVIEW,
                FileCategory.REVIEW_IMAGE
        );
        return files.stream()
                .map(File::getFileUrl)
                .collect(Collectors.toList());
    }

    private static void initializeLazyCollection(List<Review> reviews) {
        reviews.forEach(review -> {
            if (review.getReviewTags() != null && !review.getReviewTags().isEmpty()) {

                review.getReviewTags().size();
                review.getReviewTags().forEach(reviewTag -> {
                    Tag tag = reviewTag.getTag();
                    tag.getName();
                });
            }
        });
    }
}
