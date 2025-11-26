package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselStatus;

public record InstitutionCounselResponseDto(
        Long id,
        String title,
        String description,
        CounselStatus status
) {
    public static InstitutionCounselResponseDto from(InstitutionCounsel counsel) {
        return new InstitutionCounselResponseDto(
                counsel.getId(),
                counsel.getTitle(),
                counsel.getDescription(),
                counsel.getStatus()
        );
    }
}
