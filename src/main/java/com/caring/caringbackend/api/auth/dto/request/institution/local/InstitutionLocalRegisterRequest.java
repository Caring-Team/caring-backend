package com.caring.caringbackend.api.auth.dto.request.institution.local;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InstitutionLocalRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // TODO: Write required fields
}
