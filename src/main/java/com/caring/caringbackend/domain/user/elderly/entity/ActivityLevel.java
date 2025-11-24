package com.caring.caringbackend.domain.user.elderly.entity;

import lombok.Getter;

// 어르신 활동 수준
@Getter
public enum ActivityLevel {

    //예시
    HIGH("높음", "혼자 외출 가능하고 일상생활 대부분 스스로 가능"),
    MEDIUM("보통", "실내 활동 위주, 짧은 거리 보행 가능"),
    LOW("낮음", "거동이 많이 불편하고 이동 시 도움 필요"),
    BEDRIDDEN("와상", "침대에서만 생활");

    private final String description;
    private final String detail;

    ActivityLevel(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}
