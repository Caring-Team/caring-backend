package com.caring.caringbackend.domain.user.guardian.entity;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.tag.entity.MemberPreferenceTag;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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
    private MemberRole role;

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

    // 주소
    @Embedded
    private Address address;

    // 위도, 경도
    @Embedded
    private GeoPoint location;

    // 어르신 프로필 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElderlyProfile> elderlyProfiles = new ArrayList<>();

    // 선호 태그 연관관계
    @BatchSize(size = 100)  // N+1 문제 해결: 한 번에 100개씩 배치로 조회
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPreferenceTag> preferenceTags = new ArrayList<>();

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
        this.elderlyProfiles = new ArrayList<>();
        this.preferenceTags = new ArrayList<>();
    }

    /**
     * 회원 정보 수정
     */
    public void updateInfo(String name, String phoneNumber, Gender gender,
                           LocalDate birthDate, Address address, GeoPoint location) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.location = location;
    }

    /**
     * 선호 태그 추가 편의 메서드 (양방향 관계 설정)
     */
    public void addPreferenceTag(MemberPreferenceTag preferenceTag) {
        this.preferenceTags.add(preferenceTag);
    }

    /**
     * 선호 태그 전체 삭제 편의 메서드 (양방향 관계 정리)
     */
    public void clearPreferenceTags() {
        this.preferenceTags.clear();
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