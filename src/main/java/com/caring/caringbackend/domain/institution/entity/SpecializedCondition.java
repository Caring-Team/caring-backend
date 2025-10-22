package com.caring.caringbackend.domain.institution.entity;

import lombok.Getter;

@Getter
public enum SpecializedCondition {

    DEMENTIA("치매", "알츠하이머 및 치매 전문 케어"),
    CANCER("암", "암 환자 케어"),
    REHABILITATION("재활", "재활 치료 전문");
    // TODO: 추가적인 전문 질환 조건 추가

    private final String description;
    private final String detail;

    SpecializedCondition(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}

