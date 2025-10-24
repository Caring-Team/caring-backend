package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 리뷰 태그 매핑 엔티티
 * <p>
 * 리뷰에 첨부된 태그 정보를 관리합니다.
 * 한 리뷰는 여러 태그를 가질 수 있으며, 리뷰 분석 및 통계에 활용됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "tag_id"})
})
public class ReviewTagMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    public ReviewTagMapping(Review review, Tag tag) {
        this.review = review;
        this.tag = tag;
    }

    // TODO: 필요한 도메인 로직 추가
}
