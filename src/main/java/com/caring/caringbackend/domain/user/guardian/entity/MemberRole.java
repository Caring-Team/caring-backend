package com.caring.caringbackend.domain.user.guardian.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    TEMP_OAUTH("ROLE_TEMP_OAUTH", "임시 사용자"),
    TEMP_LOCAL("ROLE_TEMP_LOCAL", "임시 사용자"),
    USER("ROLE_USER", "사용자");

    private final String key;
    private final String tittle;

}
