package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.domain.institution.profile.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 기관 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionCreateRequestDto {

    // 기관 이름
    @NotBlank(message = "기관 이름은 필수입니다")
    private String name;

    // 기관 유형
    @NotNull(message = "기관 유형은 필수입니다")
    private InstitutionType institutionType;

    // 연락처
    private String phoneNumber;

    // 주소 정보
    @NotBlank(message = "도시는 필수입니다")
    private String city;

    @NotBlank(message = "거리는 필수입니다")
    private String street;

    private String zipCode;

    // 병상수
    private Integer bedCount;

    // 입소 가능 여부
    private Boolean isAdmissionAvailable;

    // 전문 질환 목록 (질환 코드 리스트)
    private List<String> specializedConditionCodes;

    // 관련 태그

    // 가격 정보
    private Integer monthlyBaseFee;      // 월 기본 요금
    private Integer admissionFee;        // 입소 비용
    private Integer monthlyMealCost;     // 식비 (월)
    private String priceNotes;           // 가격 비고

    // 운영 시간
    private String openingHours;
}
