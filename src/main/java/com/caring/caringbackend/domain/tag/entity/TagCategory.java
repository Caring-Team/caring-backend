package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

@Getter
public enum TagCategory {

    SPECIALIZATION("전문/질환"),
    SERVICE("서비스 유형"),
    OPERATION("운영 특성"),
    ENVIRONMENT("환경/시설"),
    REVIEW("리뷰 유형");

    private final String description;

    TagCategory(String description) {
        this.description = description;
    }
}
