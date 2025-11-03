package com.caring.caringbackend.api.auth.dto.request.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserOAuth2LoginRequest {

    @NotBlank
    @JsonProperty("authorization_code")
    private String authorizationCode;

    @NotBlank
    @JsonProperty("state")
    private String state;
}
