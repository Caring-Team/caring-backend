package com.caring.caringbackend.api.institution.dto.request;

import com.caring.caringbackend.global.model.Gender;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

/**
 * 요양보호사 수정 요청 DTO
 * 모든 필드 선택적 (null이 아닌 값만 수정)
 */
public record CareGiverUpdateRequestDto(
        String name,

        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        String phoneNumber,

        Gender gender,

        LocalDate birthDate,

        String experienceDetails
) { }

