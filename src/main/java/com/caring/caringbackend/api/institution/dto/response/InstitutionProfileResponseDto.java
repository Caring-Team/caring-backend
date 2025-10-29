package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;

/**
 * 기관 목록 조회 응답 DTO (Record)
 *
 * 목록 조회에 필요한 요약 정보만 포함합니다.
 */
public record InstitutionProfileResponseDto(
        Long id,
        String name,
        InstitutionType institutionType,
        String phoneNumber,
        ApprovalStatus approvalStatus,
        Boolean isAdmissionAvailable,
        Integer bedCount,

        // 주소 및 위치 정보
        Address address,
        GeoPoint location,

        // 가격 정보 (요약)
        Integer monthlyBaseFee
) {
    /**
     * Entity → DTO 변환
     */
    public static InstitutionProfileResponseDto from(Institution institution) {
        return new InstitutionProfileResponseDto(
                institution.getId(),
                institution.getName(),
                institution.getInstitutionType(),
                institution.getPhoneNumber(),
                institution.getApprovalStatus(),
                institution.getIsAdmissionAvailable(),
                institution.getBedCount(),
                institution.getAddress(),
                institution.getLocation(),
                institution.getPriceInfo() != null ? institution.getPriceInfo().getMonthlyBaseFee() : null
        );
    }
}

