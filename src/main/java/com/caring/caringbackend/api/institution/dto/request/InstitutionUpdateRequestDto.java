package com.caring.caringbackend.api.institution.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 기관 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionUpdateRequestDto {

    // 기관 이름
    @Size(max = 100, message = "기관 이름은 100자를 초과할 수 없습니다")
    private String name;

    // 연락처
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 입력 가능합니다")
    private String phoneNumber;

    // 주소 정보
    private String city;
    private String street;
    private String zipCode;

    // 병상수
    @Min(value = 0, message = "병상 수는 0 이상이어야 합니다")
    private Integer bedCount;

    // 입소 가능 여부
    private Boolean isAdmissionAvailable;
    
    // 태그 ID 목록 (선택, 최대 20개)
    @Size(max = 20, message = "태그는 최대 20개까지 선택할 수 있습니다")
    private List<Long> tagIds;

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
