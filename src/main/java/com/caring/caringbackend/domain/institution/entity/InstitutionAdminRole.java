package com.caring.caringbackend.domain.institution.entity;

import lombok.Getter;

@Getter
public enum InstitutionAdminRole {

    ADMIN("관리자"),
    STAFF("직원");

    private final String description;

    InstitutionAdminRole(String description) {
        this.description = description;
    }
}