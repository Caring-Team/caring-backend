package com.caring.caringbackend.api.internal.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class JwtTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    public HttpHeaders toHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (accessToken != null) {
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(expiresIn)
                    .sameSite("Lax")
                    .build(); //
            headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        }
        if (refreshToken != null) {
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(refreshTokenExpiresIn)
                    .sameSite("Lax")
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }
        return headers;
    }

}
