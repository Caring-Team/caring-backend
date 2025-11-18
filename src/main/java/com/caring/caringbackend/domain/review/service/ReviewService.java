package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.api.user.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.user.dto.review.request.ReviewReportRequest;
import com.caring.caringbackend.api.user.dto.review.request.ReviewUpdateRequest;
import com.caring.caringbackend.api.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.user.dto.review.response.ReviewResponse;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.entity.ReviewReport;
import com.caring.caringbackend.domain.review.repository.ReviewReportRepository;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
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

import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * 리뷰 작성
     *
     * @param memberId 회원 ID
     * @param request  리뷰 작성 요청
     * @return 작성된 리뷰 응답
     */
    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request) {
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
        // TODO: Reservation 엔티티에 completedAt 필드 추가 필요 (현재는 updatedAt 사용)
        LocalDateTime completedDate = reservation.getUpdatedAt();
        if (completedDate != null && completedDate.isBefore(LocalDateTime.now().minusDays(90))) {
            throw new BusinessException(ErrorCode.REVIEW_CREATE_EXPIRED);
        }

        // 5. 중복 리뷰 확인
        boolean alreadyExists = reviewRepository.existsByReservationIdAndMemberIdAndDeletedFalse(
                request.getReservationId(), memberId);
        if (alreadyExists) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // TODO: Reservation 엔티티에 completedAt 필드 추가 후, 완료일 기준으로 검증 필요
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

        // 9. 응답 반환
        return ReviewResponse.from(savedReview);
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

        // 3. DTO 변환
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(ReviewResponse::from)
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

        // 2. DTO 변환
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());

        // 3. 응답 반환
        return ReviewListResponse.of(reviewResponses, reviewPage);
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

        return ReviewResponse.from(review);
    }

    /**
     * 리뷰 수정
     *
     * @param memberId 회원 ID
     * @param reviewId 리뷰 ID
     * @param request  리뷰 수정 요청
     * @return 수정된 리뷰 응답
     */
    @Transactional
    public ReviewResponse updateReview(Long memberId, Long reviewId, ReviewUpdateRequest request) {
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

        // 5. 응답 반환
        return ReviewResponse.from(review);
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
}

