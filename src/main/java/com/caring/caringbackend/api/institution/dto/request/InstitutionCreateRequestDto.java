package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.domain.institution.profile.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 기관 생성 요청 DTO
 */
@Data
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
    @Pattern(regexp = "^\\+?[0-9\\-]{7,15}$", message = "유효한 연락처 형식이 아닙니다")
    private String phoneNumber;

    // 주소 정보
    @NotBlank(message = "도시는 필수입니다")
    private String city;

    @NotBlank(message = "거리는 필수입니다")
    private String street;

    private String zipCode;

    // 병상수
    @Min(value = 0, message = "병상 수는 0 이상이어야 합니다")
    @Max(value = 10000, message = "병상 수는 10000 이하이어야 합니다")
    private Integer bedCount;

    // 입소 가능 여부
    private Boolean isAdmissionAvailable;

    // 전문 질환 목록 (질환 코드 리스트)
    private List<String> specializedConditionCodes;
    
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

    // 사업자 등록번호
    @NotBlank(message = "사업자 등록번호는 필수입니다")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자 등록번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
    private String businessLicense;
}
