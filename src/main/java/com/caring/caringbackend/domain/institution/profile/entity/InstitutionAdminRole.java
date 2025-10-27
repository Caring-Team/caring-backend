package com.caring.caringbackend.domain.institution.profile.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstitutionAdminRole {

    OWNER("ROLE_INSTITUTION_OWNER", "기관장"),
    STAFF("ROLE_INSTITUTION_STAFF", "직원"),
    TEMP_INSTITUTION("ROLE_TEMP_INSTITUTION", "임시 기관 유저");

    private final String key;
    private final String titile;
}