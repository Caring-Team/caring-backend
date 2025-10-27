package com.caring.caringbackend.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRefreshRequest {
    @NotBlank
    @JsonProperty("request_token")
    private String requestToken;
}
