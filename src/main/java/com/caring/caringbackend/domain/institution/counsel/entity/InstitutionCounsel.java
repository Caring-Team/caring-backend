package com.caring.caringbackend.domain.institution.counsel.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.BaseEntity;
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
public class InstitutionCounsel extends BaseEntity {

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

    // 상태 enum 추가 가능 (예: ACTIVE, INACTIVE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselStatus status = CounselStatus.ACTIVE;

    @Builder(access = AccessLevel.PRIVATE)
    public InstitutionCounsel(Institution institution, String title, String description) {
        this.institution = institution;
        this.title = title;
        this.description = description;
    }

    public static InstitutionCounsel createInstitutionCounsel(Institution institution, String title, String description) {
        InstitutionCounsel counsel = InstitutionCounsel.builder()
                .institution(institution)
                .title(title)
                .description(description)
                .build();

        institution.addCounsel(counsel);
        return counsel;
    }

    public CounselStatus toggleStatus() {
        if (status == CounselStatus.ACTIVE) {
            status = CounselStatus.INACTIVE;
            return status;
        }

        if (status == CounselStatus.INACTIVE) {
            status = CounselStatus.ACTIVE;
            return status;
        }
        return status;
    }

    // 상담 서비스 soft delete
    public void delete() {
        this.status = CounselStatus.INACTIVE;

        if(this.isDeleted()) {
            throw new BusinessException(ErrorCode.COUNSEL_ALREADY_DELETED);
        }

        this.softDelete();
    }

    public void updateInfo(String title, String description) {
        // null이 아닌 값만 업데이트
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
    }
}
