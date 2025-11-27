package com.caring.caringbackend.domain.file.entity;

/**
 * 파일 카테고리
 * 파일의 용도를 구분합니다.
 */
public enum FileCategory {
    /**
     * 사업자 등록증
     */
    BUSINESS_LICENSE("사업자 등록증"),

    /**
     * 기관 프로필 이미지
     */
    INSTITUTION_PROFILE("기관 프로필 이미지"),

    /**
     * 회원 프로필 이미지
     */
    MEMBER_PROFILE("회원 프로필 이미지"),

    /**
     * 요양보호사 자격증
     */
    CAREGIVER_LICENSE("요양보호사 자격증"),

    /**
     * 기타 문서
     */
    DOCUMENT("기타 문서"),

    /**
     * 리뷰 첨부 이미지
     */
    REVIEW_IMAGE("리뷰 첨부 이미지"),

    /**
     * 요양 보호사 사진
     */
    CAREGIVER_PHOTO("요양 보호사 사진");

    private final String description;

    FileCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

