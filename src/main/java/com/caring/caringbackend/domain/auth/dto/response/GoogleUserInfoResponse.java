package com.caring.caringbackend.domain.auth.dto.response;

import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
    "sub": "123456789012345678901",
    "name": "홍길동,
    "given_name": "길동,
    "family_name": "홍,
    "picture": "https://lh3.googleusercontent.com/a/ABCDEFGhijklmnopQRSTUVWSyzabcdefg123456789hijklmnopqrs=s96-c",
    "email": "email1234@gmail.com",
    "email_verified": true
}
 */
public class GoogleUserInfoResponse implements OAuth2ProviderUserInfoResponse {

    private static final CredentialType type = CredentialType.OAUTH_GOOGLE;

    @JsonProperty("sub")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @Override
    public CredentialType getProviderType() {
        return type;
    }

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
