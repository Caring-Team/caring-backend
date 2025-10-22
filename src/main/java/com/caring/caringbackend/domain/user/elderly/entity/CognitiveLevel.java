package com.caring.caringbackend.domain.user.elderly.entity;

import lombok.Getter;

// 어르신 인지 수준
@Getter
public enum CognitiveLevel {

    // 예시
    NORMAL("정상", "인지 기능 정상"),
    MILD_COGNITIVE_IMPAIRMENT("경도 인지 장애", "경미한 기억력 저하"),
    MILD_DEMENTIA("경증 치매", "일상생활에 약간의 지장"),
    MODERATE_DEMENTIA("중등도 치매", "일상생활에 상당한 도움 필요"),
    SEVERE_DEMENTIA("중증 치매", "전적인 도움 필요");

    private final String description;
    private final String detail;

    CognitiveLevel(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}
