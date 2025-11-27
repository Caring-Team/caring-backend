package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.global.model.Gender;
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

    // 성별 (ISO-IEC-5218)
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

    @Builder(access = AccessLevel.PRIVATE)
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

    /**
     * 요양보호사 생성 정적 팩토리 메서드
     * 양방향 연관관계 자동 설정
     */
    public static CareGiver createCareGiver(Institution institution, String name, String email,
                                           String phoneNumber, Gender gender, LocalDate birthDate,
                                           String experienceDetails, String photoUrl) {
        CareGiver careGiver = CareGiver.builder()
                .institution(institution)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthDate(birthDate)
                .experienceDetails(experienceDetails)
                .photoUrl(photoUrl)
                .build();

        // 양방향 연관관계 설정
        institution.addCareGiver(careGiver);
        return careGiver;
    }

    /**
     * 요양보호사 정보 수정
     * null이 아닌 값만 수정
     */
    public void updateCareGiver(String name, String email, String phoneNumber,
                               Gender gender, LocalDate birthDate, String experienceDetails) {
        if (name != null) {
            this.name = name;
        }
        if (email != null) {
            this.email = email;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (experienceDetails != null) {
            this.experienceDetails = experienceDetails;
        }
    }
}
