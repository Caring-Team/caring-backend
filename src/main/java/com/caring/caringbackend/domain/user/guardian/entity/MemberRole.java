package com.caring.caringbackend.domain.user.guardian.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    TEMP_OAUTH("ROLE_TEMP_OAUTH", "임시 사용자"),
    TEMP_LOCAL("ROLE_TEMP_LOCAL", "임시 사용자"),
    USER("ROLE_USER", "사용자"),
    
    /**
     * 시스템 관리자
     * - 태그 시스템 관리 (생성/수정/삭제) 권한
     * - 시스템 전역 메타데이터 관리 권한
     */
    ADMIN("ROLE_ADMIN", "시스템 관리자");

    private final String key;
    private final String title;

}
