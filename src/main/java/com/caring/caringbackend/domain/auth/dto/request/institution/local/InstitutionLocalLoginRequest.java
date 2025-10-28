package com.caring.caringbackend.domain.auth.dto.request.institution.local;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InstitutionLocalLoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
