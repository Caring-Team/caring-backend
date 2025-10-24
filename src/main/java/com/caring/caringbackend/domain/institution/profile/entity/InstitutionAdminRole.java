package com.caring.caringbackend.domain.institution.profile.entity;

import lombok.Getter;

@Getter
public enum InstitutionAdminRole {

    OWNER("기관장"),
    STAFF("직원");

    private final String description;

    InstitutionAdminRole(String description) {
        this.description = description;
    }
}