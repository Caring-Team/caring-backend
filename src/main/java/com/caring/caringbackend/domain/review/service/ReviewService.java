package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.api.user.dto.review.request.ReviewCreateRequest;
import com.caring.caringbackend.api.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.user.dto.review.response.ReviewResponse;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
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
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뷰 작성
     *
     * @param memberId 회원 ID
     * @param request 리뷰 작성 요청
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
        // 6. 기관 조회 (Reservation -> InstitutionCounsel -> Institution)
        Institution institution = reservation.getInstitutionCounsel() != null
        ? reservation.getInstitutionCounsel().getInstitution()
        : null;

        if (institution == null) {
            throw new BusinessException(ErrorCode.INSTITUTION_NOT_FOUND);
        }
    
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

        // TODO: 태그 연결 (ReviewTagMapping 생성)

        // 8. 응답 반환
        return ReviewResponse.from(savedReview);
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
        Page<Review> reviewPage = reviewRepository.findByMemberIdAndDeletedFalseOrderByCreatedAtDesc(
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
     * @param pageable 페이징 정보
     * @return 리뷰 목록 응답
     */
    public ReviewListResponse getInstitutionReviews(Long institutionId, Pageable pageable) {
        // 1. 리뷰 목록 조회 (삭제되지 않은 리뷰만)
        // 기관 존재 여부는 리뷰 조회 시 자동으로 확인됨 (없으면 빈 목록 반환)
        Page<Review> reviewPage = reviewRepository.findByInstitutionIdAndDeletedFalseOrderByCreatedAtDesc(
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
}

