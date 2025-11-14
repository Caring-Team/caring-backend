package com.caring.caringbackend.api.institution.dto.request;

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

}
