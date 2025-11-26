package com.caring.caringbackend.api.internal.institution.dto.request;

import com.caring.caringbackend.domain.institution.profile.entity.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

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
    @JsonProperty("type")
    @NotNull(message = "기관 유형은 필수입니다")
    private InstitutionType institutionType;

    @JsonProperty("code")
    @NotNull(message = "요양기관번호는 필수입니다")
    private Long institutionCode;

    // 연락처
    @JsonProperty("phone")
    @NotBlank(message = "연락처는 필수입니다")
    private String phoneNumber;

    // 주소 정보
    @NotBlank(message = "도시는 필수입니다")
    private String city;

    @NotBlank(message = "거리는 필수입니다")
    private String street;

    @JsonProperty("zip_code")
    private String zipCode;

    // 사업자 등록번호
    @JsonProperty("business_license")
    @NotBlank(message = "사업자 등록번호는 필수입니다")
    private String businessLicense;
}
