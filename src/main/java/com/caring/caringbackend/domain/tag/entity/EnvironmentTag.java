package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

// 환경/시설 태그
@Getter
public enum EnvironmentTag {
    ELEVATOR("엘리베이터"),
    WHEELCHAIR_ACCESS("휠체어접근가능"),
    PRIVATE_ROOM("개인실"),
    SHARED_ROOM("다인실"),
    GARDEN("정원"),
    OUTDOOR_SPACE("야외공간"),
    EXERCISE_ROOM("운동실"),
    REHABILITATION_ROOM("재활치료실"),
    ENTERTAINMENT_ROOM("오락실"),
    LIBRARY("도서실"),
    CHAPEL("예배실"),
    DINING_HALL("식당"),
    CAFE("카페"),
    MEDICAL_ROOM("의료실"),
    EMERGENCY_SYSTEM("응급시스템"),
    CCTV("CCTV"),
    FIRE_SAFETY("화재안전시설"),
    AIR_CONDITIONING("냉난방시설"),
    CLEAN_FACILITY("청결한시설"),
    NEW_FACILITY("신축건물");

    private final String description;

    EnvironmentTag(String description) {
        this.description = description;
    }
}

