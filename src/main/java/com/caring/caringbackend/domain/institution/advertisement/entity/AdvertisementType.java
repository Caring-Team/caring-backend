package com.caring.caringbackend.domain.institution.advertisement.entity;

import lombok.Getter;

@Getter
public enum AdvertisementType {
    MAIN_BANNER("메인 페이지 최상단 배너", 1200, 400, 300000),
    PREMIUM_LIST("기관 목록 상단 고정 노출", 0, 0, 200000),
    SIDE_BANNER("사이드바 배너 광고", 300, 600, 100000),
    SEARCH_TOP("검색 결과 상단 노출", 0, 0, 150000);

    private final String description;
    private final int recommendedWidth;  // 권장 너비 (px)
    private final int recommendedHeight; // 권장 높이 (px)
    private final int weeklyPrice;       // 주당 가격 (원)

    AdvertisementType(String description, int recommendedWidth, int recommendedHeight, int weeklyPrice) {
        this.description = description;
        this.recommendedWidth = recommendedWidth;
        this.recommendedHeight = recommendedHeight;
        this.weeklyPrice = weeklyPrice;
    }
}
