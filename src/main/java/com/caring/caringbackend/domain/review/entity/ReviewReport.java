package com.caring.caringbackend.domain.review.entity;

import com.caring.caringbackend.domain.institution.entity.Institution;
import com.caring.caringbackend.domain.user.guardian.entity.User;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 (신고자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 기관 (신고자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    // 신고 대상 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 신고 사유
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    // 상세 내용
    @Column(length = 1000)
    private String description;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Builder
    public ReviewReport(User user, Institution institution, Review review,
                       ReportReason reason, String description) {
        // 둘 중 하나는 필수
        if (user == null && institution == null) {
            // TODO: 커스텀 예외 처리
        }
        // 둘 다 있으면 안됨
        if (user != null && institution != null) {
            // TODO: 커스텀 예외 처리
        }

        this.user = user;
        this.institution = institution;
        this.review = review;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
    }


    // 비즈니스 로직: 신고 처리
    public void approve(String adminNote) {
        this.status = ReportStatus.APPROVED;
    }

    public void reject(String adminNote) {
        this.status = ReportStatus.REJECTED;
    }

    public void process() {
        this.status = ReportStatus.PROCESSING;
    }
}
