package com.caring.caringbackend.domain.tag.entity;

import lombok.Getter;

// 리뷰 유형 태그
@Getter
public enum ReviewTag {
    KIND_STAFF("친절한직원"),
    PROFESSIONAL_CARE("전문적인케어"),
    CLEAN("청결함"),
    GOOD_FOOD("음식맛좋음"),
    VARIETY_FOOD("다양한메뉴"),
    GOOD_COMMUNICATION("소통잘됨"),
    QUICK_RESPONSE("신속한대응"),
    COMFORTABLE("편안한분위기"),
    GOOD_PROGRAMS("좋은프로그램"),
    WELL_MANAGED("잘관리됨"),
    REASONABLE_PRICE("합리적인가격"),
    GOOD_LOCATION("좋은위치"),
    FAMILY_FRIENDLY("가족친화적"),
    INDIVIDUAL_CARE("개별맞춤케어"),
    EMERGENCY_READY("응급대응우수"),
    TRANSPARENT_MANAGEMENT("투명한운영"),
    RESPECTFUL("존중하는태도"),
    WARM_ATMOSPHERE("따뜻한분위기"),
    ACTIVE_ACTIVITIES("활발한활동"),
    MEDICAL_COOPERATION("의료협력우수");

    private final String description;

    ReviewTag(String description) {
        this.description = description;
    }
}

