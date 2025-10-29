package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 요양 기관 엔티티
 * <p>
 * 요양원, 주간보호센터 등 케어 서비스를 제공하는 기관 정보를 관리합니다.
 * 기관의 기본 정보, 위치, 가격, 전문 질환, 승인 상태 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Institution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이름
    @Column(nullable = false, length = 100)
    private String name;


    // 기관 유형
    @Enumerated(EnumType.STRING)
    private InstitutionType institutionType;

    // 연락처
    @Column(length = 20)
    private String phoneNumber;

    // 주소
    @Embedded
    private Address address;

    // 위도 경도
    @Embedded
    private GeoPoint location;

    // 승인 상태
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    // 병상수
    private Integer bedCount;

    // 입소 가능 여부
    private Boolean isAdmissionAvailable;

    // 전문 질환 목록
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstitutionSpecializedCondition> specializedConditions = new ArrayList<>();

    // 기관 계정 목록 (OWNER, ADMIN, STAFF 포함) -> 기관장은 Role로 확인
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstitutionAdmin> admins = new ArrayList<>();

    // 요양사 목록
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareGiver> careGivers = new ArrayList<>();

    // 가격표
    @Embedded
    private PriceInfo priceInfo;

    // 운영 시간 (요일별 상세 시간 -> 기관에에 따라 다를 수 있어 일단 문자열로 처리)
    @Column(length = 500)
    private String openingHours;

    @Builder
    public Institution(String name, InstitutionType institutionType,
                       String phoneNumber, Address address, GeoPoint location,
                       ApprovalStatus approvalStatus, Integer bedCount,
                       Boolean isAdmissionAvailable,
                       PriceInfo priceInfo, String openingHours) {
        this.name = name;
        this.admins = new ArrayList<>();
        this.institutionType = institutionType;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.location = location;
        this.approvalStatus = approvalStatus;
        this.bedCount = bedCount;
        this.isAdmissionAvailable = isAdmissionAvailable;
        this.specializedConditions = new ArrayList<>();
        this.priceInfo = priceInfo;
        this.openingHours = openingHours;
    }

    // TODO: 필요한 도메인 추가

    /**
     * 기관장(OWNER) 조회 메서드
     */
    public InstitutionAdmin getOwner() {
        return admins.stream()
                .filter(InstitutionAdmin::isOwner)
                .findFirst()
                .orElse(null);
    }

    /**
     * 관리자 추가 편의 메서드
     */
    public void addAdmin(InstitutionAdmin admin) {
        this.admins.add(admin);
    }
}
