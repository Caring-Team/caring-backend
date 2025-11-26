package com.caring.caringbackend.api.internal.auth.dto.response;

import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
{
    "id": 1234567890,
    "connected_at": "2025-10-21T12:12:48Z",
    "kakao_account": {
        "name_needs_agreement": false,
        "name": "홍길동,
        "has_phone_number": true,
        "phone_number_needs_agreement": false,
        "phone_number": "+82 10-1234-5678",
        "has_gender": true,
        "gender_needs_agreement": false,
        "gender": "male"
    }
}
 */

public class KakaoUserInfoResponse implements OAuth2ProviderUserInfoResponse {
    private static final CredentialType type = CredentialType.OAUTH_KAKAO;

    @JsonProperty("id")
    private String id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    // ✅ 내부 클래스 (or 별도 파일로 분리 가능)
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        // ✅ Getter/Setter
        @JsonProperty("name_needs_agreement")
        private boolean nameNeedsAgreement;

        @JsonProperty("name")
        private String name;

        @JsonProperty("has_phone_number")
        private boolean hasPhoneNumber;

        @JsonProperty("phone_number_needs_agreement")
        private boolean phoneNumberNeedsAgreement;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("has_gender")
        private boolean hasGender;

        @JsonProperty("gender_needs_agreement")
        private boolean genderNeedsAgreement;

        @JsonProperty("gender")
        private String gender;
    }


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
        return kakaoAccount.getName();
    }

}
