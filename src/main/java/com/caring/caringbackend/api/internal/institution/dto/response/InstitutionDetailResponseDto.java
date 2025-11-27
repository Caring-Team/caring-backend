package com.caring.caringbackend.api.internal.institution.dto.response;

import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewResponse;
import com.caring.caringbackend.api.internal.admin.dto.response.TagResponse;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import com.caring.caringbackend.domain.file.service.FileService;
import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;
import com.caring.caringbackend.domain.institution.profile.entity.PriceInfo;
import com.caring.caringbackend.domain.tag.entity.Tag;
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

        //-------------------------------------
        // 설명
        String description,

        // 대표 사진
        String mainImageUrl,

        // 태그들
        List<TagResponse> tags,

        // 리뷰 (이름, 평점, 내용, 태그들)
        InstitutionReviewsResponseDto reviews,
        //---------------------------------------

        // 생성/수정 정보
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Entity Institution → DTO 변환 (기존 메서드 - PreSigned URL 생성 포함)
     */
    public static InstitutionDetailResponseDto from(
            Institution institution,
            FileService fileService,
            InstitutionReviewsResponseDto reviews
    ) {
        if (institution == null) {
            return null;
        }

        return new InstitutionDetailResponseDto(
                institution.getName(), // 이름
                institution.getInstitutionType(), // 기관 타입
                institution.getPhoneNumber(), // 전화번호
                institution.getApprovalStatus(), // 승인 상태
                institution.getIsAdmissionAvailable(), // 입소 가능 여부
                institution.getBedCount(), // 병상 수
                institution.getOpeningHours(), // 운영 시간
                institution.getAddress(), // 주소
                institution.getLocation(), // 위치
                institution.getPriceInfo(), // 가격 정보
                extractSpecializedConditions(institution), // 전문 질환 목록
                institution.getCounsels().stream()
                        .map(InstitutionCounselResponseDto::from)
                        .toList(), // 기관 상담 서비스 목록
                institution.getCareGivers().stream()
                        .map(careGiver -> {
                            String presignedUrl = fileService.generatePresignedUrl(careGiver.getPhotoUrl());
                            return CareGiverResponseDto.fromWithPresignedUrl(careGiver, presignedUrl);
                        }).toList(), // 요양보호사 목록
                institution.getDescription(), // 설명
                fileService.generatePresignedUrl(institution.getMainImageUrl()), // 대표 사진
                institution.getTags().stream()
                        .map(tag -> TagResponse.from(tag.getTag()))
                        .toList(), // 태그들
                reviews, // 리뷰들
                institution.getCreatedAt(), // 생성일
                institution.getUpdatedAt() // 수정일
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
