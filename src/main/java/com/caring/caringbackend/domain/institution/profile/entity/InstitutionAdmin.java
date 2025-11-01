package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기관 관리자 엔티티
 * <p>
 * 요양 기관을 관리하는 관리자 계정 정보를 관리합니다. 한 기관은 여러 관리자를 가질 수 있으며, 각 관리자는 Admin, Staff을 가집니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionAdmin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column(nullable = false)
    private String name;

    // 아이디
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    // 비밀 번호
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private LocalDate birthDate;

    // 역할
    // 역할 (ADMIN, STAFF)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionAdminRole role;

    @Column(nullable = false, unique = true)
    private String duplicationInformation;

    @Builder
    public InstitutionAdmin(Institution institution, String name, String username,
                            String passwordHash, String phoneNumber, LocalDate birthDate, InstitutionAdminRole role,
                            String duplicationInformation) {
        this.institution = institution;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.role = role;
        this.duplicationInformation = duplicationInformation;
    }

    // TODO: 필요한 도메인 로직 작성
    public static String makeDuplicationInformation(String name, LocalDate brithDate, String phoneNumber) {
        return name + brithDate + phoneNumber;
    }

    /**
     * OWNER 여부 확인
     */
    public boolean isOwner() {
        return role == InstitutionAdminRole.OWNER;
    }
}
