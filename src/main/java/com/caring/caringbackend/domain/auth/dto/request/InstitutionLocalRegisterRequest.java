package com.caring.caringbackend.domain.auth.dto.request;

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
