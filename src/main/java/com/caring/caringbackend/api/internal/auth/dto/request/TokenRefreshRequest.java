package com.caring.caringbackend.api.internal.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenRefreshRequest {
    @NotBlank
    @JsonProperty("request_token")
    private String requestToken;
}
