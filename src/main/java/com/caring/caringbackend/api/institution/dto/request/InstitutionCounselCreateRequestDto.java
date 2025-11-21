package com.caring.caringbackend.api.institution.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionCounselCreateRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
