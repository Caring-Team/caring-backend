package com.caring.caringbackend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InstitutionLocalLoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
