package com.caring.caringbackend.domain.institution.advertisement.entity;

import lombok.Getter;

@Getter
public enum AdvertisementStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("승인 거부");

    private final String description;

    AdvertisementStatus(String description) {
        this.description = description;
    }
}
