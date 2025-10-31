package com.caring.caringbackend.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
    "access_token": "Aa1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "token_type": "bearer",
    "refresh_token": "Aa1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "expires_in": 21599,
    "scope": "gender name phone_number",
    "refresh_token_expires_in": 5183999
}
 */
public class KakaoTokenResponse implements OAuth2ProviderTokenResponse {

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
