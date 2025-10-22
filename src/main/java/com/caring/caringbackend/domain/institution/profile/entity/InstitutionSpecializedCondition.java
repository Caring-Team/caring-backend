package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기관 전문 질환 엔티티
 * <p>
 * 요양 기관이 전문적으로 케어할 수 있는 질환 정보를 관리합니다.
 * 치매, 뇌졸중, 파킨슨병 등 기관이 특화된 질환과 그에 대한 설명을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionSpecializedCondition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 전문 질환 타입
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecializedCondition conditionType;

    // 관련 설명
    @Column(length = 500)
    private String description;

    // 활성화 여부
    private Boolean isActive;

    @Builder
    public InstitutionSpecializedCondition(Institution institution,
                                           SpecializedCondition conditionType,
                                           Integer expertiseLevel,
                                           String description,
                                           Boolean isActive) {
        this.institution = institution;
        this.conditionType = conditionType;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }

    // TODO: 필요한 도메인 로직 작성
}
