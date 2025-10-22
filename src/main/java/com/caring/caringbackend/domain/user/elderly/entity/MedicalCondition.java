package com.caring.caringbackend.domain.user.elderly.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicalCondition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어르신
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_profile_id", nullable = false)
    private ElderlyProfile elderlyProfile;

    // 질병명
    @Column(nullable = false, length = 100)
    private String conditionName;

    // 상세 설명
    @Column(length = 500)
    private String description;

    // 진단 날짜
    private LocalDate diagnosed_at;

    @Builder
    public MedicalCondition(ElderlyProfile elderlyProfile, String conditionName,
                            String description, LocalDate diagnosed_at) {
        this.elderlyProfile = elderlyProfile;
        this.conditionName = conditionName;
        this.description = description;
        this.diagnosed_at = diagnosed_at;
    }

    // TODO: 필요한 도메인 로직 작성
}
