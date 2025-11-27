package com.caring.caringbackend.domain.review.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.tag.entity.ReviewTag;
import com.caring.caringbackend.domain.tag.entity.ReviewTagMapping;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰 엔티티
 * <p>
 * 회원이 요양 기관에 대해 작성한 리뷰 정보를 관리합니다.
 * 예약을 기반으로 작성되며, 별점, 내용, 태그 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 기관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 내용
    @Column(nullable = false, length = 500)
    private String content;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    // 신고 여부
    @Column(nullable = false)
    private boolean reported;

    // 리뷰 태그 매핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTagMapping> reviewTags = new ArrayList<>();

    @Builder
    public Review(Reservation reservation, Member member, Institution institution,
                  String content, int rating) {
        this.reservation = reservation;
        this.member = member;
        this.institution = institution;
        this.content = content;
        this.rating = rating;
        this.reported = false;
    }

    /**
     * 리뷰 내용 및 별점 수정
     *
     * @param content 수정할 리뷰 내용
     * @param rating  수정할 별점 (1~5)
     */
    public void updateContent(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }

    /**
     * 리뷰 신고 표시
     */
    public void markReported() {
        this.reported = true;
    }

    /**
     * 리뷰 신고 해제
     */
    public void clearReport() {
        this.reported = false;
    }

    /**
     * 리뷰 소유자 검증
     * */
    public boolean isOwnedBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    public void addReviewTagMapping(ReviewTagMapping reviewTagMapping) {
        this.reviewTags.add(reviewTagMapping);
    }
}