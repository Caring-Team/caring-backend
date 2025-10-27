package com.caring.caringbackend.domain.user.guardian.entity;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role -> TEMP_USER, USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.TEMP_USER;

    // 이름
    @Column(length = 100, nullable = false)
    private String name;

    // 핸드폰
    @Column(length = 20, unique = true)
    private String phoneNumber;

    /**
     * NICE 같은 본인인증 서비스 제공 업체에서 제공하는 중복 가입 정보 (CI로 대체 가능) <br>
     * 중복가입 방지는 본인인증 서비스 없이는 불가능함 <br>
     * 추후 본인인증 서비스 도입을 위한 컬럼 <br>
     * 현재 임시로 이름, 생년월일, 전화번호를 조합하여 사용 <br>
     */
    @Column(nullable = false, unique = true, updatable = false)
    private String duplicationInformation;

    // 성별
    private Gender gender;

    // 생년월일
    @Column(nullable = false)
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
    public Member(MemberRole role, String name, String phoneNumber, String duplicationInformation,
                  Gender gender, LocalDate birthDate, Address address, GeoPoint location) {
        this.role = role;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.duplicationInformation = duplicationInformation;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.location = location;
    }

    // TODO: 필요한 도메인 로직 작성

    /**
     * Make custom duplication information.
     * @param name          name of member
     * @param brithDate     birthdate of member
     * @param phoneNumber   phone number of member
     * @return simple combination of name, birthdate, phone number
     */
    public static String makeDuplicationInformation(String name, LocalDate brithDate, String phoneNumber) {
        return name + brithDate + phoneNumber;
    }
}