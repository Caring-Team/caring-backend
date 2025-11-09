package com.caring.caringbackend.domain.review.repository;

import com.caring.caringbackend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

