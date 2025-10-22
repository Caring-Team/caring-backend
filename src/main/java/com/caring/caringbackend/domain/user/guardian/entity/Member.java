package com.caring.caringbackend.domain.user.guardian.entity;

import com.caring.caringbackend.domain.user.Address;
import com.caring.caringbackend.domain.user.Gender;
import com.caring.caringbackend.domain.user.GeoPoint;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 생년월일
    private LocalDate birthDate;

    // 프로필 사진
    private String profileImageUrl;

    // 주소
    @Embedded
    private Address address;

    // 위도, 경도
    @Embedded
    private GeoPoint location;

    // AuthCredential 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthCredential> authCredentials = new ArrayList<>();

    // 어르신 프로필 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElderlyProfile> elderlyProfiles = new ArrayList<>();

    @Builder
    public Member(String email, String name, String phoneNumber, Gender gender,
                  LocalDate birthDate, String profileImageUrl, Address address, GeoPoint location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
        this.location = location;
        this.authCredentials = new ArrayList<>();
    }

    // TODO: 필요한 도메인 로직 작성
}