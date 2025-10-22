package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

// 리뷰 태그 (N:M 매핑 테이블)
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

