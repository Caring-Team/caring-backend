package com.caring.caringbackend.api.internal.institution.dto.response;

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
    /**
     * CareGiver 엔티티를 DTO로 변환 (원본 photoUrl 사용)
     */
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

    /**
     * CareGiver 엔티티를 DTO로 변환 (PreSigned URL 사용)
     *
     * @param careGiver CareGiver 엔티티
     * @param presignedPhotoUrl PreSigned URL (Service에서 생성)
     * @return CareGiverResponseDto
     */
    public static CareGiverResponseDto fromWithPresignedUrl(CareGiver careGiver, String presignedPhotoUrl) {
        return new CareGiverResponseDto(
                careGiver.getId(),
                careGiver.getName(),
                careGiver.getEmail(),
                careGiver.getPhoneNumber(),
                careGiver.getGender(),
                careGiver.getBirthDate(),
                careGiver.getExperienceDetails(),
                presignedPhotoUrl
        );
    }
}

