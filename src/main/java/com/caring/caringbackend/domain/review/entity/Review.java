package com.caring.caringbackend.domain.review.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 엔티티
 * <p>
 * 회원이 요양 기관에 대해 작성한 리뷰 정보를 관리합니다.
 * 예약을 기반으로 작성되며, 별점, 내용, 태그 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    // 기관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 내용
    @Column(nullable = false, length = 500)
    private String content;

    // 별점
    @Column(nullable = false)
    private int rating;

    // 태그
    // TODO: 태그 도입
}