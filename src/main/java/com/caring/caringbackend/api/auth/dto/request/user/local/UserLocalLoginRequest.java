package com.caring.caringbackend.api.auth.dto.request.user.local;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLocalLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
