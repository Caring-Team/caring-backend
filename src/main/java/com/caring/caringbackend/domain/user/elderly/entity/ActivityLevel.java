package com.caring.caringbackend.domain.user.elderly.entity;

import lombok.Getter;

// 어르신 활동 수준
@Getter
public enum ActivityLevel {

    //예시
    HIGH("높음", "독립적으로 활동 가능"),
    MEDIUM("보통", "일부 도움이 필요"),
    LOW("낮음", "상당한 도움이 필요"),
    BEDRIDDEN("와상", "침대에서만 생활");

    private final String description;
    private final String detail;

    ActivityLevel(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}
