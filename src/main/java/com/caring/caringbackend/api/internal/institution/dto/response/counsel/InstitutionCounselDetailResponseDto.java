package com.caring.caringbackend.api.internal.institution.dto.response.counsel;

import com.caring.caringbackend.api.internal.institution.dto.CounselHourDto;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InstitutionCounselDetailResponseDto {

    private Long id;

    private String title;

    private String description;

    @JsonProperty("min_reservable_days_before")
    Integer minReservableDaysBefore;

    @JsonProperty("max_reservable_days_before")
    Integer maxReservableDaysBefore;

    @JsonProperty("status")
    private CounselStatus counselStatus;

    private CounselTimeUnit unit;

    @JsonProperty("counsel_hours")
    private Set<CounselHourDto> counselHours;

}
