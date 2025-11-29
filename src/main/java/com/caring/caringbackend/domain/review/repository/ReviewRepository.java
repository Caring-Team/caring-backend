package com.caring.caringbackend.domain.review.repository;

import com.caring.caringbackend.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     * <p>
     * N+1 문제 방지를 위해 member, institution 연관 관계를 즉시 로딩합니다.
     */
    @EntityGraph(attributePaths = {"member", "institution"})
    Page<Review> findByMemberIdAndDeletedFalseAndReportedFalseOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    /**
     * 특정 기관의 리뷰 목록 조회 (삭제되지 않은 리뷰만, 신고되지 않은 리뷰만)
     * <p>
     * N+1 문제 방지를 위해 member, institution 연관 관계를 즉시 로딩합니다.
     * Pageable의 sort 파라미터로 정렬 순서를 지정할 수 있습니다.
     */
    @EntityGraph(attributePaths = {"member", "institution"})
    Page<Review> findByInstitutionIdAndDeletedFalseAndReportedFalse(Long institutionId, Pageable pageable);

    /**
     * 특정 회원의 최근 리뷰 상위 5개 조회 (삭제되지 않은 리뷰만, 신고되지 않은 리뷰만)
     * <p>
     * 마이페이지용으로 사용됩니다.
     */
    @EntityGraph(attributePaths = {"institution", "reservation"})
    List<Review> findTop5ByMemberIdAndDeletedFalseAndReportedFalseOrderByCreatedAtDesc(Long memberId);

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

    @Query("""
            select r
            from Review r
            left join fetch r.member m
            left join fetch r.institution i
            left join fetch r.reservation res
            left join fetch res.counselDetail cd
            left join fetch cd.institutionCounsel ic
            where i.id = :institutionId
            and r.deleted = false
            order by r.createdAt desc
            """)
    List<Review> findByIdWithFetches(Long institutionId);
}

