package com.caring.caringbackend.domain.tag.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 기관 태그 엔티티
 * <p>
 * 요양 기관이 제공하는 서비스나 특성을 태그로 관리합니다.
 * 기관과 태그 간의 다대다 관계를 표현하며, 추가 설명을 포함할 수 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"institution_id", "tag_id"})
})
public class InstitutionTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // 추가 설명
    @Column(length = 500)
    private String additionalInfo;

    @Builder
    public InstitutionTag(Institution institution, Tag tag, String additionalInfo) {
        Objects.requireNonNull(institution, "기관은 필수입니다.");
        Objects.requireNonNull(tag, "태그는 필수입니다.");

        this.institution = institution;
        this.tag = tag;
        this.additionalInfo = additionalInfo;
    }

    // TODO: 필요한 도메인 로직 추가
}
