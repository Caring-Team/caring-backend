package com.caring.caringbackend.domain.institution.profile.entity;

import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
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
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstitutionType institutionType;

    // 연락처
    @Column(nullable = false, length = 20)
    private String phoneNumber;

    // 주소
    @Embedded
    private Address address;

    // 위도 경도
    @Embedded
    private GeoPoint location;

    // 승인 상태
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    // 병상수
    private Integer bedCount;

    // 입소 가능 여부
    @Column(nullable = false)
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

    @Builder(access = AccessLevel.PRIVATE)
    private Institution(String name, InstitutionType institutionType,
                        String phoneNumber, Address address, GeoPoint location,
                        ApprovalStatus approvalStatus, Integer bedCount,
                        Boolean isAdmissionAvailable,
                        PriceInfo priceInfo, String openingHours) {

        // 도메인 비즈니스 규칙 검증
        validate(phoneNumber, bedCount);

        this.name = name;
        this.admins = new ArrayList<>();
        this.institutionType = institutionType;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.location = location;
        this.approvalStatus = approvalStatus != null ? approvalStatus : ApprovalStatus.PENDING;
        this.bedCount = bedCount;
        this.isAdmissionAvailable = isAdmissionAvailable != null ? isAdmissionAvailable : false;
        this.specializedConditions = new ArrayList<>();
        this.careGivers = new ArrayList<>();
        this.priceInfo = priceInfo;
        this.openingHours = openingHours;
    }

    /**
     * 기관 생성 정적 팩토리 메서드
     */
    public static Institution createInstitution(
            String name,
            InstitutionType institutionType,
            String phoneNumber,
            Address address,
            GeoPoint location,
            Integer bedCount,
            Boolean isAdmissionAvailable,
            PriceInfo priceInfo,
            String openingHours) {

        return new Institution(
                name,
                institutionType,
                phoneNumber,
                address,
                location,
                ApprovalStatus.PENDING,
                bedCount,
                isAdmissionAvailable,
                priceInfo,
                openingHours
        );
    }

    /**
     * 비즈니스 규칙 검증
     */
    private void validate(String phoneNumber, Integer bedCount) {
        // 전화번호 길이 검증 (형식은 DTO에서, 길이는 도메인 규칙)
        if (phoneNumber.replaceAll("-", "").length() < 9) {
            throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER, "전화번호는 최소 9자리 이상이어야 합니다.");
        }

        // 병상 수 최대값 검증 (비즈니스 규칙) TODO: 요구사항에 따라 수정
        if (bedCount != null && bedCount > 1000) {
            throw new BusinessException(ErrorCode.INVALID_BED_COUNT, "병상 수는 1000개를 초과할 수 없습니다.");
        }
    }

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

    /**
     * 승인 여부 체크 메서드
     */
    public boolean isApproved() {
        return this.approvalStatus == ApprovalStatus.APPROVED;
    }

    /**
     * 기관 승인 처리 메서드
     */
    public void approveInstitution() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    /**
     * 기관 정보 수정 메서드
     */
    public void updateInstitution(
            String name,
            String phoneNumber,
            Address address,
            GeoPoint location,
            Integer bedCount,
            Boolean isAdmissionAvailable,
            PriceInfo priceInfo,
            String openingHours) {

        // 비즈니스 규칙 검증
        if (phoneNumber != null) {
            validate(phoneNumber, bedCount);
        }

        // 수정 가능한 필드만 업데이트
        if (name != null) {
            this.name = name;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (address != null) {
            this.address = address;
            this.location = location; // 주소 변경 시 위치도 함께 업데이트
        }
        if (bedCount != null) {
            this.bedCount = bedCount;
        }
        if (isAdmissionAvailable != null) {
            this.isAdmissionAvailable = isAdmissionAvailable;
        }
        if (priceInfo != null) {
            this.priceInfo = priceInfo;
        }
        if (openingHours != null) {
            this.openingHours = openingHours;
        }
    }

    /**
     * 입소 가능 여부 변경 메서드
     */
    public void changeAdmissionAvailability(Boolean isAdmissionAvailable) {
        if (isAdmissionAvailable != null) {
            this.isAdmissionAvailable = isAdmissionAvailable;
        }
    }

    /**
     * 기관 삭제 (Soft Delete)
     * <p>
     * 논리적 삭제를 수행합니다.
     * 입소 가능 여부를 false로 변경
     */
    public void deleteInstitution() {
        if (!isActive()) {
            throw new BusinessException(ErrorCode.INSTITUTION_ALREADY_DELETED);
        }

        // 삭제 시 입소 불가능으로 변경
        this.isAdmissionAvailable = false;
        softDelete();
    }

}
