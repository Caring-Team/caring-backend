package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.api.institution.dto.CounselHourDto;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InstitutionCounselCreateRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @JsonProperty("counsel_hours")
    @NotEmpty
    @Valid
    private Set<CounselHourDto> counselHours;

    @Min(0)
    @Max(7)
    @NotNull
    @JsonProperty("min_reservable_days_before")
    private Integer minReservableDaysBefore;

    @Min(0)
    @Max(30)
    @NotNull
    @JsonProperty("max_reservable_days_before")
    private Integer maxReservableDaysBefore;

    @NotNull
    private CounselTimeUnit unit;
}
