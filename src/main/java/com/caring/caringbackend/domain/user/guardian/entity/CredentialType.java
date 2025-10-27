package com.caring.caringbackend.domain.user.guardian.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CredentialType {
    LOCAL("local"),
    LOCAL_INSTITUTION("local-institution"),
    OAUTH_KAKAO("kakao"),
    OAUTH_NAVER("naver"),
    OAUTH_GOOGLE("google"),
    UNKNOWN("unknown");

    private final String key;

    public static CredentialType fromKey(String key) {
        for (CredentialType type : CredentialType.values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
