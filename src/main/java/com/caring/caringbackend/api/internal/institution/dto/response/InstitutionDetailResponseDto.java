package com.caring.caringbackend.api.internal.institution.dto.response;

import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.domain.institution.profile.entity.PriceInfo;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 기관 상세 응답 DTO (Record)
 */
public record InstitutionDetailResponseDto(
        // 기본 정보
        String name,
        InstitutionType institutionType,
        String phoneNumber,
        ApprovalStatus approvalStatus,
        Boolean isAdmissionAvailable,
        Integer bedCount,
        String openingHours,

        // 주소, 위치, 가격 정보
        Address address,
        GeoPoint location,
        PriceInfo priceInfo,

        // 전문 질환 목록
        List<String> specializedConditions,

        // 요양보호사 목록
        // TODO: 추후 요양보호사 목록 추가

        // 생성/수정 정보
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Entity Institution → DTO 변환
     */
    public static InstitutionDetailResponseDto entityToDto(Institution institution) {
        if (institution == null) {
            return null;
        }

        return new InstitutionDetailResponseDto(
                institution.getName(),
                institution.getInstitutionType(),
                institution.getPhoneNumber(),
                institution.getApprovalStatus(),
                institution.getIsAdmissionAvailable(),
                institution.getBedCount(),
                institution.getOpeningHours(),
                institution.getAddress(),
                institution.getLocation(),
                institution.getPriceInfo(),
                extractSpecializedConditions(institution),
                institution.getCreatedAt(),
                institution.getUpdatedAt()
        );
    }

    /**
     * 전문 질환 목록 추출
     */
    private static List<String> extractSpecializedConditions(Institution institution) {
        if (institution.getSpecializedConditions() == null) {
            return List.of();
        }

        return institution.getSpecializedConditions().stream()
                .map(condition -> condition.getConditionType().name())
                .collect(Collectors.toList());
    }
}
