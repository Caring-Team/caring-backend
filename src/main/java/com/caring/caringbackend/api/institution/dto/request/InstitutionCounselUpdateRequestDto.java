package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.api.institution.dto.CounselHourDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionCounselUpdateRequestDto {

    private String title;
    private String description;

    @Min(0)
    @Max(7)
    @JsonProperty("min_reservable_days_before")
    Integer minReservableDaysBefore;

    @Min(0)
    @Max(30)
    @JsonProperty("max_reservable_days_before")
    Integer maxReservableDaysBefore;

    @JsonProperty("counsel_hours")
    private Set<CounselHourDto> counselHours;

}
