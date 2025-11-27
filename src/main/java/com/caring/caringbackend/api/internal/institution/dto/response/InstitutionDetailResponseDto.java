package com.caring.caringbackend.api.internal.institution.dto.response;

import com.caring.caringbackend.domain.file.service.FileService;
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

        // 기관 상담 서비스 목록
        List<InstitutionCounselResponseDto> counselServices,

        // 요양보호사 목록
        List<CareGiverResponseDto> careGivers,

        // 생성/수정 정보
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Entity Institution → DTO 변환 (기존 메서드 - PreSigned URL 생성 포함)
     *
     */
    public static InstitutionDetailResponseDto from(Institution institution, FileService fileService) {
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
                institution.getCounsels().stream()
                        .map(InstitutionCounselResponseDto::from)
                        .toList(),
                institution.getCareGivers().stream()
                        .map(careGiver -> {
                            String presignedUrl = fileService.generatePresignedUrl(careGiver.getPhotoUrl());
                            return CareGiverResponseDto.fromWithPresignedUrl(careGiver, presignedUrl);
                        })
                        .toList(),
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
