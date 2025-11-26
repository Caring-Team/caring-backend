package com.caring.caringbackend.api.institution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CounselHourDto {

    @NotEmpty
    private Set<DayOfWeek> days;

    @JsonProperty("start_time")
    @NotNull
    private LocalTime startTime;

    @JsonProperty("end_time")
    @NotNull
    private LocalTime endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CounselHourDto that = (CounselHourDto) o;
        return Objects.equals(days, that.days) && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(days, startTime, endTime);
    }
}
