package com.caring.caringbackend.domain.user.guardian.entity;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원(보호자) 엔티티
 * <p>
 * 케어링 서비스를 이용하는 보호자 회원 정보를 관리합니다.
 * 한 명의 회원은 여러 어르신 프로필과 여러 인증 정보를 가질 수 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role -> TEMP_USER, USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    // 이메일
    @Email
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    // 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 핸드폰
    @Column(length = 20)
    private String phoneNumber;

    // 성별
    @Column(nullable = false)
    private Gender gender;

    // 생년월일
    private LocalDate birthDate;

    // 프로필 사진 (TODO: 추후 구현 예정)
    // private String profileImageUrl;

    // 주소
    @Embedded
    private Address address;

    // 위도, 경도
    @Embedded
    private GeoPoint location;

    // 어르신 프로필 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElderlyProfile> elderlyProfiles = new ArrayList<>();

    @Builder
    public Member(String email, String name, String phoneNumber, Gender gender,
                  LocalDate birthDate, Address address, GeoPoint location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.location = location;
    }

    // TODO: 필요한 도메인 로직 작성
}