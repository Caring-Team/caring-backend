package com.caring.caringbackend.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
  "access_token": "Aa1-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "refresh_token": "Aa1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "token_type": "bearer",
  "expires_in": "3600"
}

 */

public class NaverTokenResponse implements OAuth2ProviderTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
