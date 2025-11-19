package com.caring.caringbackend.api.institution.dto.response.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 광고 상세 응답 DTO
 */
@Schema(description = "광고 상세 응답")
public record AdvertisementResponseDto(
        @Schema(description = "광고 ID")
        Long id,

        @Schema(description = "기관 ID")
        Long institutionId,

        @Schema(description = "기관명")
        String institutionName,

        @Schema(description = "광고 유형")
        AdvertisementType type,

        @Schema(description = "광고 상태")
        AdvertisementStatus status,

        @Schema(description = "광고 제목")
        String title,

        @Schema(description = "광고 설명")
        String description,

        @Schema(description = "배너 이미지 URL")
        String bannerImageUrl,

        @Schema(description = "시작 일시")
        LocalDateTime startDateTime,

        @Schema(description = "종료 일시")
        LocalDateTime endDateTime,

        @Schema(description = "생성 일시")
        LocalDateTime createdAt,


        @Schema(description = "취소 사유")
        String cancelReason,

        @Schema(description = "관리자 메모")
        String adminMemo,

        @Schema(description = "현재 활성 상태 여부")
        boolean isActive
) {
    /**
     * Entity를 DTO로 변환
     */
    public static AdvertisementResponseDto from(InstitutionAdvertisement ad) {
        return new AdvertisementResponseDto(
                ad.getId(),
                ad.getInstitution().getId(),
                ad.getInstitution().getName(),
                ad.getType(),
                ad.getStatus(),
                ad.getTitle(),
                ad.getDescription(),
                ad.getBannerImageUrl(),
                ad.getStartDateTime(),
                ad.getEndDateTime(),
                ad.getCreatedAt(),
                ad.getCancelReason(),
                ad.getAdminMemo(),
                ad.isActive()
        );
    }
}

