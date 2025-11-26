package com.caring.caringbackend.api.internal.auth.dto.request.institution.local;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionLocalLoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
