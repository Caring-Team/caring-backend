package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 선호 태그 엔티티
 * <p>
 * 회원이 선호하는 요양 기관의 특성을 태그로 관리합니다.
 * 기관 추천 및 매칭 시스템에 활용됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tag_id"})
})
public class MemberPreferenceTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    public MemberPreferenceTag(Member member, Tag tag) {
        this.member = member;
        this.tag = tag;
    }

    // TODO: 필요한 도메인 로직 추가
}
