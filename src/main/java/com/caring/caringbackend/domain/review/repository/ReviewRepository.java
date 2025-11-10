package com.caring.caringbackend.domain.review.repository;

import com.caring.caringbackend.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리뷰 Repository 인터페이스
 * <p>
 * Review 엔티티에 대한 데이터 액세스 계층입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 회원의 리뷰 수 조회 (삭제되지 않은 리뷰만)
     *
     * @param memberId 회원 ID
     * @return 리뷰 수
     */
    long countByMemberIdAndDeletedFalse(Long memberId);

    /**
     * 특정 회원의 리뷰 목록 조회 (삭제되지 않은 리뷰만, 최신순)
     *
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록
     */
    Page<Review> findByMemberIdAndDeletedFalseOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    /**
     * 특정 기관의 리뷰 목록 조회 (삭제되지 않은 리뷰만, 최신순)
     * 
     * TODO: isReported 필드 추가 후 신고된 리뷰 제외 로직 추가 필요
     *
     * @param institutionId 기관 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록
     */
    Page<Review> findByInstitutionIdAndDeletedFalseOrderByCreatedAtDesc(Long institutionId, Pageable pageable);

    /**
     * 예약 ID와 회원 ID로 리뷰 존재 여부 확인 (중복 리뷰 체크용)
     *
     * @param reservationId 예약 ID
     * @param memberId 회원 ID
     * @return 리뷰 존재 여부
     */
    boolean existsByReservationIdAndMemberIdAndDeletedFalse(Long reservationId, Long memberId);

    /**
     * 리뷰 ID와 회원 ID로 리뷰 조회 (작성자 확인용)
     *
     * @param reviewId 리뷰 ID
     * @param memberId 회원 ID
     * @return 리뷰
     */
    Optional<Review> findByIdAndMemberIdAndDeletedFalse(Long reviewId, Long memberId);

    /**
     * 리뷰 ID로 리뷰 조회 (삭제되지 않은 리뷰만)
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰
     */
    Optional<Review> findByIdAndDeletedFalse(Long reviewId);
}

