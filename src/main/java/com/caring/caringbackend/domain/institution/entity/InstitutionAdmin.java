package com.caring.caringbackend.domain.institution.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기관 관리자 엔티티
 * <p>
 * 요양 기관을 관리하는 관리자 계정 정보를 관리합니다.
 * 한 기관은 여러 관리자를 가질 수 있으며, 각 관리자는 Admin, Staff을 가집니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 이메일
    @Email
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    // 비밀 번호
    @Column(nullable = false, length = 255)
    private String passwordHash;

    // 역할
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionAdminRole role;

    @Builder
    public InstitutionAdmin(Institution institution, String email,
                            String passwordHash, InstitutionAdminRole role) {
        this.institution = institution;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // TODO: 필요한 도메인 로직 작성
}
