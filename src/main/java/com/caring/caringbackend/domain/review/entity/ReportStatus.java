package com.caring.caringbackend.domain.review.entity;

import lombok.Getter;

// 신고 처리 상태
@Getter
public enum ReportStatus {
    PENDING("대기 중", "신고 접수 후 검토 대기 중"),
    PROCESSING("처리 중", "관리자가 검토 중"),
    APPROVED("승인", "신고가 승인되어 리뷰가 삭제되거나 제재됨"),
    REJECTED("반려", "신고가 부적절하여 반려됨");

    private final String description;
    private final String detail;

    ReportStatus(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}

