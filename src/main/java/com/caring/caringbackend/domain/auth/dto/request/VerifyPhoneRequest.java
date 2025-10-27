package com.caring.caringbackend.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class VerifyPhoneRequest {

    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @NotBlank
    @JsonProperty("phone")
    private String phoneNumber;

    @NotBlank
    @JsonProperty("code")
    private String code;
}
