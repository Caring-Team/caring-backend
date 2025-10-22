package com.caring.caringbackend.domain.institution.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

