package com.caring.caringbackend.domain.institution.counsel.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상담 요청 상태
 * - ACTIVE: 상담 진행 가능 (채팅 가능)
 * - CLOSED: 상담 완전 종료
 */
@Getter
@RequiredArgsConstructor
public enum ConsultRequestStatus {
    
    ACTIVE("활성", "상담 진행 가능 상태 (채팅 가능)"),
    CLOSED("종료", "상담이 완전히 종료된 상태");
    
    private final String description;
    private final String detail;
}

