package com.caring.caringbackend.api.internal.institution.dto.request;

import com.caring.caringbackend.global.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 요양사 생성 요청 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CareGiverCreateRequestDto {
    // 요양사 이름
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    // 이메일
    private String email;

    // 핸드폰 번호
    @NotNull(message = "핸드폰 번호는 필수입니다")
    @Pattern(regexp = "^010(?:[-.\\s]?\\d{4}){2}$")
    private String phoneNumber;

    // 성별 (ISO-IEC-5218)
    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    // 생년월일
    private LocalDate birthDate;

    // 경력 특이사항
    private String experienceDetails;

}
