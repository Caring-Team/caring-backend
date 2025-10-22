package com.caring.caringbackend.domain.review.entity;

import lombok.Getter;

// 신고 사유
@Getter
public enum ReportReason {
    SPAM("스팸/광고", "스팸성 또는 광고성 리뷰"),
    ABUSIVE_LANGUAGE("욕설/비방", "욕설 또는 비방이 포함된 리뷰"),
    FALSE_INFORMATION("허위 정보", "사실이 아닌 정보가 포함된 리뷰"),
    PRIVACY_VIOLATION("개인정보 노출", "개인정보가 노출된 리뷰"),
    IRRELEVANT("관련 없는 내용", "기관과 관련 없는 내용"),
    DUPLICATE("중복 리뷰", "동일한 내용의 중복 리뷰"),
    INAPPROPRIATE("부적절한 내용", "기타 부적절한 내용"),
    OTHER("기타", "기타 사유");

    private final String description;
    private final String detail;

    ReportReason(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}

