package com.caring.caringbackend.domain.institution.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가격 정보 임베디드 타입
 *
 * 요양 기관의 이용 가격 정보를 관리합니다.
 * 월 기본 요금, 입소 비용, 식비 등의 정보를 포함합니다.
 */
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
