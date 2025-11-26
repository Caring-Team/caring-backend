package com.caring.caringbackend.api.internal.institution.dto.response.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 광고 요약 정보 DTO (목록용)
 */
@Schema(description = "광고 요약 정보")
public record AdvertisementSummaryDto(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "광고 유형")
        AdvertisementType type,

        @Schema(description = "상태")
        AdvertisementStatus status,

        @Schema(description = "광고 제목")
        String title,

        @Schema(description = "시작 일시")
        LocalDateTime startDateTime,

        @Schema(description = "종료 일시")
        LocalDateTime endDateTime,

        @Schema(description = "생성 일시")
        LocalDateTime createdAt,

        @Schema(description = "현재 활성 상태 여부")
        boolean isActive
) {
    /**
     * Request Entity를 요약 DTO로 변환
     */
    public static AdvertisementSummaryDto fromRequest(InstitutionAdvertisementRequest request) {
        return new AdvertisementSummaryDto(
                request.getId(),
                request.getType(),
                request.getStatus(),
                request.getTitle(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getCreatedAt(),
                false // Request는 활성 상태 없음
        );
    }

    /**
     * Advertisement Entity를 요약 DTO로 변환
     */
    public static AdvertisementSummaryDto fromAdvertisement(InstitutionAdvertisement ad) {
        return new AdvertisementSummaryDto(
                ad.getId(),
                ad.getType(),
                ad.getStatus(),
                ad.getTitle(),
                ad.getStartDateTime(),
                ad.getEndDateTime(),
                ad.getCreatedAt(),
                ad.isActive()
        );
    }
}

