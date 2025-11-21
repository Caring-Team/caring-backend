package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.profile.entity.CareGiver;
import com.caring.caringbackend.global.model.Gender;

import java.time.LocalDate;

/**
 * 요양보호사 응답 DTO
 */
public record CareGiverResponseDto(
        Long id,
        String name,
        String email,
        String phoneNumber,
        Gender gender,
        LocalDate birthDate,
        String experienceDetails,
        String photoUrl
) {
    public static CareGiverResponseDto from(CareGiver careGiver) {
        return new CareGiverResponseDto(
                careGiver.getId(),
                careGiver.getName(),
                careGiver.getEmail(),
                careGiver.getPhoneNumber(),
                careGiver.getGender(),
                careGiver.getBirthDate(),
                careGiver.getExperienceDetails(),
                careGiver.getPhotoUrl()
        );
    }
}

