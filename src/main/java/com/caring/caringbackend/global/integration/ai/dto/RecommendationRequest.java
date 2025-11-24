package com.caring.caringbackend.global.integration.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 서버 추천 요청 DTO
 *
 * @author 나의찬
 * @since 2025-11-23
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    /**
     * 회원 정보
     */
    private MemberInfo member;

    /**
     * 어르신 프로필 정보
     */
    private ElderlyInfo elderly;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        /**
         * 회원 ID
         */
        private Long memberId;

        /**
         * 회원 이름
         */
        private String name;

        /**
         * 전문 질환 관련 선호 태그
         */
        private List<String> preferredSpecializedDiseases;

        /**
         * 서비스 유형 선호 태그
         */
        private List<String> preferredServiceTypes;

        /**
         * 운영 특성 선호 태그
         */
        private List<String> preferredOperationalFeatures;

        /**
         * 환경/시설 선호 태그
         */
        private List<String> preferredFacilityFeatures;

        /**
         * 위치 정보 (위도)
         */
        private Double latitude;

        /**
         * 위치 정보 (경도)
         */
        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElderlyInfo {
        /**
         * 어르신 프로필 ID
         */
        private Long elderlyProfileId;

        /**
         * 어르신 이름
         */
        private String name;

        /**
         * 성별
         */
        private String gender;

        /**
         * 생년월일
         */
        private String birthDate;

        /**
         * 혈액형
         */
        private String bloodType;

        /**
         * 연락처
         */
        private String phoneNumber;

        /**
         * 장기요양등급
         */
        private String longTermCareGrade;

        /**
         * 활동 수준
         */
        private String activityLevel;

        /**
         * 인지 수준
         */
        private String cognitiveLevel;

        /**
         * 특이사항
         */
        private String notes;

        /**
         * 주소
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
    }
}

