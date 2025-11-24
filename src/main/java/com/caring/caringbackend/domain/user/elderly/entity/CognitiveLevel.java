package com.caring.caringbackend.domain.user.elderly.entity;

import lombok.Getter;

// 어르신 인지 수준
@Getter
public enum CognitiveLevel {

    // 예시
    NORMAL("정상", "인지 기능에 큰 문제 없음"),
    MILD_COGNITIVE_IMPAIRMENT("경도 인지 장애", "경미한 기억력 저하가 있습니다."),
    MILD_DEMENTIA("경증 치매", "일상생활에 약간의 지장가는 수준입니다."),
    MODERATE_DEMENTIA("중등도 치매", "치매 초기, 가끔 기억력 저하와 길 잃습니다."),
    SEVERE_DEMENTIA("중증 치매", "인지 저하가 심하고 지속적인 보호가 필요합니다.");

    private final String description;
    private final String detail;

    CognitiveLevel(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}
