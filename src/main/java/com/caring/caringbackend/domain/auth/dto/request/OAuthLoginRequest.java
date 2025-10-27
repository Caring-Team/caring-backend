package com.caring.caringbackend.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OAuthLoginRequest {

    @NotBlank
    @JsonProperty("authorization_code")
    private String authorizationCode;

    @NotBlank
    @JsonProperty("state")
    private String state;
}
