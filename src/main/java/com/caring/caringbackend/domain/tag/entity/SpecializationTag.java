package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

// 전문/질환 태그
@Getter
public enum SpecializationTag {
    DEMENTIA("치매"),
    STROKE("뇌졸중"),
    PARKINSONS("파킨슨병"),
    DIABETES("당뇨"),
    HYPERTENSION("고혈압"),
    BEDSORE("욕창"),
    TUBE_FEEDING("경관영양"),
    CATHETER("도뇨관"),
    RESPIRATORY("호흡기질환"),
    CANCER("암"),
    REHABILITATION("재활"),
    MENTAL_HEALTH("정신건강"),
    PALLIATIVE("완화케어"),
    FRACTURE("골절"),
    ARTHRITIS("관절염");

    private final String description;

    SpecializationTag(String description) {
        this.description = description;
    }
}

