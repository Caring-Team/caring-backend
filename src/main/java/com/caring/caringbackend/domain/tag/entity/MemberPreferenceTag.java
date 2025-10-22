package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 사용자 선호 태그 (사용자가 원하는 기관 특성)
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

