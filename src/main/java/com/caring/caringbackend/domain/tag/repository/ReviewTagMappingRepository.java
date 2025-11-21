package com.caring.caringbackend.domain.tag.repository;

import com.caring.caringbackend.domain.tag.entity.ReviewTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 리뷰 태그 매핑 Repository 인터페이스
 * <p>
 * ReviewTagMapping 엔티티에 대한 데이터 액세스 계층입니다.
 *
 * @author 윤다인
 * @since 2025-11-18
 */
@Repository
public interface ReviewTagMappingRepository extends JpaRepository<ReviewTagMapping, Long> {

    /**
     * 리뷰 ID로 태그 매핑 목록 조회
     *
     * @param reviewId 리뷰 ID
     * @return 태그 매핑 목록
     */
    @Query("SELECT rtm FROM ReviewTagMapping rtm JOIN FETCH rtm.tag WHERE rtm.review.id = :reviewId")
    List<ReviewTagMapping> findByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 리뷰 ID로 태그 매핑 삭제 (리뷰 수정 시 사용)
     *
     * @param reviewId 리뷰 ID
     */
    @Modifying
    @Query("DELETE FROM ReviewTagMapping rtm WHERE rtm.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
}

