package com.caring.caringbackend.domain.institution.entity;

import lombok.Getter;

@Getter
public enum InstitutionType {
    DAY_CARE_CENTER("데이케어센터"),
    NURSING_HOME("요양원"),
    HOME_CARE_SERVICE("제가 돌봄 서비스");

    private final String description;

    InstitutionType(String description) {
        this.description = description;
    }
}
