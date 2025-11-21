package com.caring.caringbackend.domain.file.entity;

/**
 * 참조 엔티티 타입
 * 파일이 어떤 엔티티와 연관되어 있는지 구분합니다.
 */
public enum ReferenceType {
    /**
     * 기관
     */
    INSTITUTION("기관"),

    /**
     * 회원
     */
    MEMBER("회원"),

    /**
     * 요양보호사
     */
    CAREGIVER("요양보호사"),

    /**
     * 리뷰
     */
    REVIEW("리뷰"),

    /**
     * 없음 (임시 파일 등)
     */
    NONE("없음");

    private final String description;

    ReferenceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

