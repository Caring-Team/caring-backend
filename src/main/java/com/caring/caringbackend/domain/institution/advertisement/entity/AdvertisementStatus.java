package com.caring.caringbackend.domain.institution.advertisement.entity;

import lombok.Getter;

@Getter
public enum AdvertisementStatus {
    // 승인 전의 상태
    REQUEST_PENDING("승인 대기"),
    REQUEST_APPROVED("승인 완료"),
    REQUEST_REJECTED("승인 거부"),

    // 승인 후의 상태
    ADVERTISEMENT_PENDING("광고 대기중"),
    ADVERTISEMENT_ACTIVE("광고 진행중"),
    ADVERTISEMENT_ENDED("광고 종료"),
    ADVERTISEMENT_CANCELED("광고 취소");

    private final String description;

    AdvertisementStatus(String description) {
        this.description = description;
    }
}
