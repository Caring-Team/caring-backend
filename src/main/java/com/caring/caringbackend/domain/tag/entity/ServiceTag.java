package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

// 서비스 유형 태그
@Getter
public enum ServiceTag {
    DAYCARE("주간보호"),
    SHORTTERM("단기보호"),
    LONGTERM("장기요양"),
    VISITING_CARE("방문요양"),
    VISITING_NURSING("방문간호"),
    VISITING_BATHING("방문목욕"),
    HOME_CARE("재가돌봄"),
    MEAL_DELIVERY("식사배달"),
    REHABILITATION_SERVICE("재활서비스"),
    MEDICAL_SERVICE("의료서비스"),
    EMERGENCY_CARE("응급돌봄"),
    RESPITE_CARE("휴식돌봄");

    private final String description;

    ServiceTag(String description) {
        this.description = description;
    }
}

