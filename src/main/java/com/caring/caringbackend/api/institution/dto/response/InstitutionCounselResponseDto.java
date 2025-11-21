package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;

public record InstitutionCounselResponseDto(
        Long id,
        String title,
        String description
) {
    public static InstitutionCounselResponseDto from(InstitutionCounsel counsel) {
        return new InstitutionCounselResponseDto(
                counsel.getId(),
                counsel.getTitle(),
                counsel.getDescription() );
    }
}
