package com.caring.caringbackend.domain.institution.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceInfo {

    // 월 기본 요금
    private Integer monthlyBaseFee;

    // 입소 비용
    private Integer admissionFee;

    // 식비 (월)
    private Integer monthlyMealCost;

    // 비고
    private String priceNotes;

    @Builder
    public PriceInfo(Integer monthlyBaseFee, Integer admissionFee,
                    Integer monthlyMealCost, Integer monthlySnackCost,
                    Integer additionalCost, String priceNotes) {
        this.monthlyBaseFee = monthlyBaseFee;
        this.admissionFee = admissionFee;
        this.monthlyMealCost = monthlyMealCost;
        this.priceNotes = priceNotes;
    }

    // TODO: 추가적인 가격 정보 필드 및 도메인 메서드 구현
}

