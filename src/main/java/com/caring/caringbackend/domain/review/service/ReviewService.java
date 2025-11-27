package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.api.internal.Member.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.internal.Member.dto.review.request.ReviewReportRequest;
import com.caring.caringbackend.api.internal.Member.dto.review.request.ReviewUpdateRequest;
import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewResponse;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.entity.ReviewReport;
import com.caring.caringbackend.domain.review.repository.ReviewReportRepository;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import com.caring.caringbackend.domain.file.entity.File;
import com.caring.caringbackend.domain.file.entity.FileCategory;
import com.caring.caringbackend.domain.file.entity.ReferenceType;
import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.domain.tag.entity.ReviewTagMapping;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.repository.ReviewTagMappingRepository;
import com.caring.caringbackend.domain.tag.repository.TagRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ⭐ 리뷰(Review) 비즈니스 로직을 처리하는 서비스
 *
 * @author 윤다인
 * @since 2025-11-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final ReviewTagMappingRepository reviewTagMappingRepository;
    private final FileService fileService;

    private static final int MAX_REVIEW_IMAGES = 5;

    /**
     * 리뷰 작성
     *
     * @param memberId 회원 ID
     * @param request  리뷰 작성 요청
     * @param images   리뷰 이미지 파일 목록 (최대 5개)
     * @return 작성된 리뷰 응답
     */
    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request, List<MultipartFile> images) {
        // 1. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 예약 조회 및 검증
        Reservation reservation = reservationRepository.findByIdAndMemberId(
                        request.getReservationId(), memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // 3. 예약 완료 여부 확인
        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_COMPLETED);
        }

        // 4. 예약 완료 후 90일 이내인지 확인
        LocalDateTime completedDate = reservation.getCompletedAt();
        if (completedDate != null && completedDate.isBefore(LocalDateTime.now().minusDays(90))) {
            throw new BusinessException(ErrorCode.REVIEW_CREATE_EXPIRED);
        }

        // 5. 중복 리뷰 확인
        boolean alreadyExists = reviewRepository.existsByReservationIdAndMemberIdAndDeletedFalse(
                request.getReservationId(), memberId);
        if (alreadyExists) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 6. 기관 조회
        Institution institution = getInstitution(reservation);

        // 7. 리뷰 생성
        Review review = Review.builder()
                .reservation(reservation)
                .member(member)
                .institution(institution)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);

        log.info("리뷰 작성 완료: reviewId={}, memberId={}, institutionId={}, rating={}",
                savedReview.getId(), memberId, institution.getId(), request.getRating());

        // 8. 태그 연결 (ReviewTagMapping 생성)
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveReviewTags(savedReview, request.getTagIds());
        }

        // 9. 이미지 업로드 및 저장
        List<String> imageUrls = uploadReviewImages(images, savedReview.getId());

        // 10. 태그 및 이미지 포함 응답 반환
        List<Tag> tags = reviewTagMappingRepository.findByReviewId(savedReview.getId()).stream()
                .map(ReviewTagMapping::getTag)
                .collect(Collectors.toList());
        return ReviewResponse.fromWithTagsAndImages(savedReview, tags, imageUrls);
    }

    private static Institution getInstitution(Reservation reservation) {
        // 6. 기관 조회 (Reservation -> InstitutionCounsel -> Institution)
        Institution institution = reservation.getCounselDetail().getInstitutionCounsel().getInstitution();
        if (institution == null) {
            throw new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND);
        }
        return institution;
    }

    /**
     * 내가 작성한 리뷰 목록 조회
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록 응답
     */
    public ReviewListResponse getMyReviews(Long memberId, Pageable pageable) {
        // 1. 회원 존재 확인
        memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 리뷰 목록 조회
        Page<Review> reviewPage = reviewRepository.findByMemberIdAndDeletedFalseAndReportedFalseOrderByCreatedAtDesc(
                memberId, pageable);

        // 3. DTO 변환 (태그 및 이미지 포함)
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(review -> {
                    List<Tag> tags = reviewTagMappingRepository.findByReviewId(review.getId()).stream()
                            .map(ReviewTagMapping::getTag)
                            .collect(Collectors.toList());
                    List<String> imageUrls = getReviewImageUrls(review.getId());
                    return ReviewResponse.fromWithTagsAndImages(review, tags, imageUrls);
                })
                .collect(Collectors.toList());

        // 4. 응답 반환
        return ReviewListResponse.of(reviewResponses, reviewPage);
    }

    /**
     * 기관의 리뷰 목록 조회 (공개)
     *
     * @param institutionId 기관 ID
     * @param pageable      페이징 정보 (정렬: createdAt, rating 지원)
     * @return 리뷰 목록 응답
     */
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

    public InstitutionReviewsResponseDto getInstitutionDetailReviews(Long institutionId) {
        // 리뷰에 reservation, member, institution를 한번에 다 가져온다.
        List<Review> reviews = reviewRepository.findByIdWithFetches(institutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        List<InstitutionReviewResponseDto> reviewResponses = reviews.stream()
                .map(InstitutionReviewResponseDto::from)
                .toList();

        return InstitutionReviewsResponseDto.of(reviewResponses);
    }

    /**
     * 내 리뷰 상세 조회
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 응답
     */
    public ReviewResponse getMyReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findByIdAndMemberIdAndDeletedFalse(reviewId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰에 연결된 태그 조회
        List<Tag> tags = reviewTagMappingRepository.findByReviewId(reviewId).stream()
                .map(ReviewTagMapping::getTag)
                .collect(Collectors.toList());

        // 리뷰 이미지 URL 조회
        List<String> imageUrls = getReviewImageUrls(reviewId);

        return ReviewResponse.fromWithTagsAndImages(review, tags, imageUrls);
    }

    /**
     * 리뷰 상세 조회
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 응답
     */
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰에 연결된 태그 조회
        List<Tag> tags = reviewTagMappingRepository.findByReviewId(reviewId).stream()
                .map(ReviewTagMapping::getTag)
                .collect(Collectors.toList());

        // 리뷰 이미지 URL 조회
        List<String> imageUrls = getReviewImageUrls(reviewId);

        return ReviewResponse.fromWithTagsAndImages(review, tags, imageUrls);
    }

    /**
     * 리뷰 수정
     *
     * @param memberId 회원 ID
     * @param reviewId 리뷰 ID
     * @param request  리뷰 수정 요청
     * @param images   리뷰 이미지 파일 목록 (최대 5개)
     * @return 수정된 리뷰 응답
     */
    @Transactional
    public ReviewResponse updateReview(Long memberId, Long reviewId, ReviewUpdateRequest request, List<MultipartFile> images) {
        // 1. 리뷰 조회 및 작성자 확인
        Review review = reviewRepository.findByIdAndMemberIdAndDeletedFalse(reviewId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED));

        // 2. 수정 제한일 확인 (작성 후 30일 이내만 수정 가능)
        LocalDateTime createdAt = review.getCreatedAt();
        if (createdAt != null && createdAt.isBefore(LocalDateTime.now().minusDays(30))) {
            throw new BusinessException(ErrorCode.REVIEW_EDIT_EXPIRED);
        }

        // 3. 리뷰 내용 및 별점 수정
        review.updateContent(request.getContent(), request.getRating());

        log.info("리뷰 수정 완료: reviewId={}, memberId={}, rating={}",
                reviewId, memberId, request.getRating());

        // 4. 태그 업데이트 (기존 태그 삭제 후 재생성)
        reviewTagMappingRepository.deleteByReviewId(reviewId);
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            saveReviewTags(review, request.getTagIds());
        }

        // 5. 이미지 업데이트 (기존 이미지 삭제 후 재업로드)
        deleteReviewImages(reviewId);
        List<String> imageUrls = uploadReviewImages(images, reviewId);

        // 6. 태그 및 이미지 포함 응답 반환
        List<Tag> tags = reviewTagMappingRepository.findByReviewId(reviewId).stream()
                .map(ReviewTagMapping::getTag)
                .collect(Collectors.toList());
        return ReviewResponse.fromWithTagsAndImages(review, tags, imageUrls);
    }

    /**
     * 리뷰 삭제 (Soft Delete)
     *
     * @param memberId 회원 ID
     * @param reviewId 리뷰 ID
     */
    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {
        // 1. 리뷰 조회 및 작성자 확인
        Review review = reviewRepository.findByIdAndMemberIdAndDeletedFalse(reviewId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED));

        // 2. 소프트 삭제 처리
        review.softDelete();

        log.info("리뷰 삭제 완료: reviewId={}, memberId={}", reviewId, memberId);
    }

    /**
     * 리뷰 신고
     *
     * @param memberId 회원 ID (신고자)
     * @param reviewId 리뷰 ID
     * @param request  리뷰 신고 요청
     */
    @Transactional
    public void reportReview(Long memberId, Long reviewId, ReviewReportRequest request) {
        // 1. 회원 존재 확인
        Member reporter = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 리뷰 조회
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 3. 본인 리뷰는 신고 불가
        if (review.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.REVIEW_SELF_REPORT_DENIED);
        }

        // 4. 중복 신고 방지 (동일 회원이 같은 리뷰를 이미 신고한 경우)
        boolean alreadyReported = reviewReportRepository.existsByReviewIdAndMemberIdAndDeletedFalse(
                reviewId, memberId);
        if (alreadyReported) {
            throw new BusinessException(ErrorCode.REVIEW_REPORT_ALREADY_EXISTS);
        }

        // 5. 리뷰 신고 생성
        ReviewReport reviewReport = ReviewReport.builder()
                .member(reporter)
                .institution(null) // 회원 신고이므로 institution은 null
                .review(review)
                .reason(request.getReportReason())
                .description(request.getDescription())
                .build();

        review.markReported();
        reviewReportRepository.save(reviewReport);

        log.info("리뷰 신고 완료: reportId={}, reviewId={}, reporterId={}, reason={}",
                reviewReport.getId(), reviewId, memberId, request.getReportReason());
    }

    /**
     * 리뷰 태그 저장 헬퍼 메서드
     *
     * @param review 리뷰
     * @param tagIds 태그 ID 목록
     */
    private void saveReviewTags(Review review, List<Long> tagIds) {
        // 1. 태그 조회
        List<Tag> tags = tagRepository.findAllByIdIn(tagIds);

        // 2. 존재하지 않는 태그 ID 검증
        if (tags.size() != tagIds.size()) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }

        // 3. ReviewTagMapping 생성 및 저장
        List<ReviewTagMapping> mappings = tags.stream()
                .map(tag -> ReviewTagMapping.builder()
                        .review(review)
                        .tag(tag)
                        .build())
                .collect(Collectors.toList());

        reviewTagMappingRepository.saveAll(mappings);

        log.debug("리뷰 태그 저장 완료: reviewId={}, tagCount={}", review.getId(), mappings.size());
    }

    /**
     * 리뷰 이미지 업로드 헬퍼 메서드
     *
     * @param images   업로드할 이미지 파일 목록
     * @param reviewId 리뷰 ID
     * @return 업로드된 이미지 URL 목록
     */
    private List<String> uploadReviewImages(List<MultipartFile> images, Long reviewId) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        // 이미지 개수 제한 검증
        if (images.size() > MAX_REVIEW_IMAGES) {
            throw new BusinessException(ErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED);
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            File uploadedFile = fileService.uploadFileWithMetadata(
                    image,
                    FileCategory.REVIEW_IMAGE,
                    reviewId,
                    ReferenceType.REVIEW
            );
            imageUrls.add(uploadedFile.getFileUrl());
        }

        log.info("리뷰 이미지 업로드 완료: reviewId={}, imageCount={}", reviewId, imageUrls.size());
        return imageUrls;
    }

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

    /**
     * 리뷰 이미지 삭제 헬퍼 메서드
     *
     * @param reviewId 리뷰 ID
     */
    private void deleteReviewImages(Long reviewId) {
        List<File> files = fileService.getFilesByReferenceAndCategory(
                reviewId,
                ReferenceType.REVIEW,
                FileCategory.REVIEW_IMAGE
        );

        for (File file : files) {
            fileService.deleteFile(file.getId());
        }

        log.info("리뷰 이미지 삭제 완료: reviewId={}, deletedCount={}", reviewId, files.size());
    }

    private static void validateReviewOwner(Long memberId, Review review) {
        if (!review.isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
    }
}

