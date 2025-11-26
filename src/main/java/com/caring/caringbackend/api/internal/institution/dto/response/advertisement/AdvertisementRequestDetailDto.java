package com.caring.caringbackend.api.internal.institution.dto.response.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 광고 신청 상세 응답 DTO
 */
@Schema(description = "광고 신청 상세 응답")
public record AdvertisementRequestDetailDto(
        @Schema(description = "신청 ID")
        Long requestId,

        @Schema(description = "기관 ID")
        Long institutionId,

        @Schema(description = "기관명")
        String institutionName,

        @Schema(description = "광고 유형")
        AdvertisementType type,

        @Schema(description = "신청 상태")
        AdvertisementStatus status,

        @Schema(description = "광고 제목")
        String title,

        @Schema(description = "광고 설명")
        String description,

        @Schema(description = "배너 이미지 URL")
        String bannerImageUrl,

        @Schema(description = "시작 예정 일시")
        LocalDateTime startDateTime,

        @Schema(description = "종료 예정 일시")
        LocalDateTime endDateTime,

        @Schema(description = "신청 일시")
        LocalDateTime createdAt,

        @Schema(description = "처리 일시 (승인/거절)")
        LocalDateTime processedAt,

        @Schema(description = "거절 사유")
        String rejectionReason,

        @Schema(description = "관리자 메모")
        String adminMemo
) {
    /**
     * Entity를 DTO로 변환
     */
    public static AdvertisementRequestDetailDto from(InstitutionAdvertisementRequest request) {
        return new AdvertisementRequestDetailDto(
                request.getId(),
                request.getInstitution().getId(),
                request.getInstitution().getName(),
                request.getType(),
                request.getStatus(),
                request.getTitle(),
                request.getDescription(),
                request.getBannerImageUrl(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getCreatedAt(),
                request.getProcessedAt(),
                request.getRejectionReason(),
                request.getAdminMemo()
        );
    }
}

