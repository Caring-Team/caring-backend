package com.caring.caringbackend.domain.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 메시지 발신자 유형
 * - MEMBER: 회원 (보호자)
 * - INSTITUTION_ADMIN: 기관 관리자 (기관장, 직원)
 */
@Getter
@RequiredArgsConstructor
public enum SenderType {
    
    MEMBER("회원", "보호자가 보낸 메시지"),
    INSTITUTION_ADMIN("기관 관리자", "기관 관리자(기관장, 직원)가 보낸 메시지");
    
    private final String description;
    private final String detail;
}

