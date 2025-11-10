package com.caring.caringbackend.domain.review.repository;

import com.caring.caringbackend.domain.review.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리뷰 신고 Repository 인터페이스
 * <p>
 * ReviewReport 엔티티에 대한 데이터 액세스 계층입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    /**
     * 리뷰 ID와 회원 ID로 신고 존재 여부 확인 (중복 신고 방지용)
     *
     * @param reviewId 리뷰 ID
     * @param memberId 회원 ID
     * @return 신고 존재 여부
     */
    boolean existsByReviewIdAndMemberIdAndDeletedFalse(Long reviewId, Long memberId);

    /**
     * 리뷰 ID와 회원 ID로 신고 조회
     *
     * @param reviewId 리뷰 ID
     * @param memberId 회원 ID
     * @return 신고
     */
    Optional<ReviewReport> findByReviewIdAndMemberIdAndDeletedFalse(Long reviewId, Long memberId);
}

