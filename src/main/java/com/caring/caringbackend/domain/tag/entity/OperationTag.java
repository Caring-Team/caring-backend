package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

// 운영 특성 태그
@Getter
public enum OperationTag {
    FEMALE_ONLY("여성전용"),
    MALE_ONLY("남성전용"),
    MIXED("남녀공용"),
    DEMENTIA_SPECIALIZED("치매전담"),
    CERTIFIED_STAFF("자격증보유직원"),
    NURSE_24H("24시간간호사"),
    DOCTOR_RESIDENT("의사상주"),
    NUTRITIONIST("영양사배치"),
    PHYSICAL_THERAPIST("물리치료사"),
    PARKING_AVAILABLE("주차가능"),
    SHUTTLE_BUS("셔틀버스"),
    PET_FRIENDLY("반려동물가능"),
    RELIGIOUS_NEUTRAL("종교중립"),
    SMALL_SCALE("소규모"),
    LARGE_SCALE("대규모");

    private final String description;

    OperationTag(String description) {
        this.description = description;
    }
}

