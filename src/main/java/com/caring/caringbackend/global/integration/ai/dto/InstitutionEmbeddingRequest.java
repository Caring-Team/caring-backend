package com.caring.caringbackend.global.integration.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 서버로 전송할 기관 임베딩 요청 DTO
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionEmbeddingRequest {

    /**
     * 기관 ID
     */
    private Long institutionId;

    /**
     * 기관 이름
     */
    private String name;

    /**
     * 기관 타입
     */
    private String institutionType;

    /**
     * 주소 정보 (city, street, zipCode 결합)
     */
    private String address;

    /**
     * 위도
     */
    private Double latitude;

    /**
     * 경도
     */
    private Double longitude;

    /**
     * 침대 수
     */
    private Integer bedCount;

    /**
     * 월 기본료
     */
    private Integer monthlyBaseFee;

    /**
     * 입소비
     */
    private Integer admissionFee;

    /**
     * 월 식비
     */
    private Integer monthlyMealCost;

    /**
     * 운영 시간
     */
    private String openingHours;

    /**
     * 전문 질환 태그 목록 (SPECIALIZATION)
     */
    private List<String> specializedDiseases;

    /**
     * 서비스 유형 태그 목록 (SERVICE)
     */
    private List<String> serviceTypes;

    /**
     * 운영 특성 태그 목록 (OPERATION)
     */
    private List<String> operationalFeatures;

    /**
     * 환경/시설 태그 목록 (ENVIRONMENT)
     */
    private List<String> facilityFeatures;

    /**
     * 기관 설명
     */
    private String description;

    /**
     * 입소 가능 여부
     */
    private Boolean isAdmissionAvailable;
}

