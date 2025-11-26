package com.caring.caringbackend.api.internal.auth.dto.response;

import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
{
    "resultcode": "00",
    "message": "success",
    "response": {
        "id": "Aa1-aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "nickname": "홍길동",
        "profile_image": "https://ssl.pstatic.net/static/pwe/address/img_profile.png",
        "email": "email@naver.com",
        "name": "홍길동"
    }
}
 */

public class NaverUserInfoResponse implements OAuth2ProviderUserInfoResponse {
    private static final CredentialType type = CredentialType.OAUTH_NAVER;

    @JsonProperty("resultcode")
    private String resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("id")
        private String id;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("email")
        private String email;

        @JsonProperty("name")
        private String name;
    }

    @Override
    public CredentialType getProviderType() {
        return type;
    }

    @Override
    public String getUserId() {
        return response.getId();
    }

    @Override
    public String getName() {
        return response.getName();
    }
}
