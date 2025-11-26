package com.caring.caringbackend.api.internal.institution.dto.request;

import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기관 검색 필터 DTO
 *
 * 다양한 조건으로 기관을 검색할 수 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionSearchFilter {

    @Parameter(description = "기관 이름 (부분 검색)")
    private String name;

    @Parameter(description = "기관 유형")
    private InstitutionType institutionType;

    @Parameter(description = "입소 가능 여부")
    private Boolean isAdmissionAvailable;

    @Parameter(description = "도시 (부분 검색)")
    private String city;

    @Parameter(description = "최대 월 기본 요금")
    private Integer maxMonthlyFee;

    @Parameter(description = "위도 (거리 기반 검색)")
    private Double latitude;

    @Parameter(description = "경도 (거리 기반 검색)")
    private Double longitude;

    @Parameter(description = "반경 (km)")
    private Double radiusKm;
}
