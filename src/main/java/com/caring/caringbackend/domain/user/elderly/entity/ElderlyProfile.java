package com.caring.caringbackend.domain.user.elderly.entity;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 어르신 프로필 엔티티
 * <p>
 * 케어 서비스를 받을 어르신의 정보를 관리합니다.
 * 회원(보호자)은 여러 어르신 프로필을 등록할 수 있습니다.
 * 어르신의 건강 상태, 활동 수준, 인지 수준 등의 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ElderlyProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 성별
    @Column(nullable = false)
    private Gender gender;

    // 생년 월일
    private LocalDate birthDate;

    // 혈액형
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    // 연락처
    @Column(length = 20)
    private String phoneNumber;

    // 활동 수준
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    // 인지 수준
    @Enumerated(EnumType.STRING)
    private CognitiveLevel cognitiveLevel;

    // 선호 케어 타입
    // TODO: Tagging 시스템 도입 후 수정

    // 특이사항
    @Column(length = 500)
    private String notes;

    // 주소
    @Embedded
    private Address address;

    // 위도 경도
    @Embedded
    private GeoPoint location;

    @Builder
    public ElderlyProfile(Member member, String name, Gender gender, LocalDate birthDate,
                          BloodType bloodType, String phoneNumber,
                          ActivityLevel activityLevel, CognitiveLevel cognitiveLevel,
                          String notes, Address address, GeoPoint location) {
        this.member = member;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.bloodType = bloodType;
        this.phoneNumber = phoneNumber;
        this.activityLevel = activityLevel;
        this.cognitiveLevel = cognitiveLevel;
        this.notes = notes;
        this.address = address;
        this.location = location;
    }

    // TODO: 필요한 도메인 로직 작성
}
