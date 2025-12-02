package com.caring.caringbackend.api.internal.auth.dto.request.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserOAuth2LoginRequest {

    @NotBlank
    @JsonProperty("authorization_code")
    private String accessToken;
}
