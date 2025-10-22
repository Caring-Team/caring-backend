package com.caring.caringbackend.domain.institution.counsel.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기관 상담 서비스 엔티티
 * <p>
 * 요양 기관이 제공하는 상담 서비스 정보를 관리합니다.
 * 상담 서비스의 이름, 설명, 일정 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionCounsel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 상담 서비스 이름
    @Column(nullable = false, length = 100)
    private String title;

    // 상담 서비스 설명
    @Column(length = 500)
    private String description;

    @Builder
    public InstitutionCounsel(Institution institution, String title, String description) {
        this.institution = institution;
        this.title = title;
        this.description = description;
    }

    // TODO: 필요한 도메인 로직 작성
}
