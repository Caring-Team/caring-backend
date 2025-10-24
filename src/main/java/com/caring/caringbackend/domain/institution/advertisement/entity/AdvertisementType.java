package com.caring.caringbackend.domain.institution.advertisement.entity;

import lombok.Getter;

@Getter
public enum AdvertisementType {
    BANNER("배너 광고");
    // TODO: 추가적인 광고 유형 추가

    private final String description;

    AdvertisementType(String description) {
        this.description = description;
    }
}
