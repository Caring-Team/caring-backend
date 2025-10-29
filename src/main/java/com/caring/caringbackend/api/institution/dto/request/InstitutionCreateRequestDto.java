package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.domain.institution.profile.entity.*;
import jakarta.validation.constraints.*;
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
    @Size(max = 100, message = "기관 이름은 100자를 초과할 수 없습니다")
    private String name;

    // 기관 유형
    @NotNull(message = "기관 유형은 필수입니다")
    private InstitutionType institutionType;

    // 연락처
    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 입력 가능합니다")
    private String phoneNumber;

    // 주소 정보
    @NotBlank(message = "도시는 필수입니다")
    private String city;

    @NotBlank(message = "거리는 필수입니다")
    private String street;

    private String zipCode;

    // 병상수
    @Min(value = 0, message = "병상 수는 0 이상이어야 합니다")
    private Integer bedCount;

    // 입소 가능 여부
    private Boolean isAdmissionAvailable;

    // 전문 질환 목록 (질환 코드 리스트)
    private List<String> specializedConditionCodes;

    // 가격 정보
    @Min(value = 0, message = "월 기본 요금은 0 이상이어야 합니다")
    private Integer monthlyBaseFee;

    @Min(value = 0, message = "입소 비용은 0 이상이어야 합니다")
    private Integer admissionFee;

    @Min(value = 0, message = "월 식비는 0 이상이어야 합니다")
    private Integer monthlyMealCost;

    private String priceNotes;

    // 운영 시간
    private String openingHours;
}
