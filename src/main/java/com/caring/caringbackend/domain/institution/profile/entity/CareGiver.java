package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.domain.user.Gender;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 요양사 엔티티
 * <p>
 * 요양 기관에 소속된 요양사(간병인) 정보를 관리합니다.
 * 요양사의 기본 정보, 경력, 전문성 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareGiver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    // 요양사 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 이메일
    @Column(length = 100)
    private String email;

    // 핸드폰 번호
    @Column(length = 20)
    private String phoneNumber;

    // 성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    // 생년월일
    private LocalDate birthDate;

    // 경력 특이사항
    @Column(length = 500)
    private String experienceDetails;

    // 사진
    @Column(length = 255)
    private String photoUrl;

    @Builder
    public CareGiver(Institution institution, String name, String email,
                     String phoneNumber, Gender gender, LocalDate birthDate,
                     String experienceDetails, String photoUrl) {
        this.institution = institution;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.experienceDetails = experienceDetails;
        this.photoUrl = photoUrl;
    }

    // TODO: 필요한 도메인 로직 작성
}
