package com.caring.caringbackend.api.institution.dto.response.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 활성 광고 정보 DTO (공개 API용)
 */
@Schema(description = "활성 광고 정보")
public record ActiveAdvertisementDto(
        @Schema(description = "광고 ID")
        Long id,

        @Schema(description = "기관 ID")
        Long institutionId,

        @Schema(description = "기관명")
        String institutionName,

        @Schema(description = "광고 유형")
        AdvertisementType type,

        @Schema(description = "광고 제목")
        String title,

        @Schema(description = "광고 설명")
        String description,

        @Schema(description = "배너 이미지 URL")
        String bannerImageUrl,

        @Schema(description = "종료 예정 시간")
        LocalDateTime endDateTime
) {
    /**
     * Entity를 활성 광고 DTO로 변환
     */
    public static ActiveAdvertisementDto from(InstitutionAdvertisement ad) {
        return new ActiveAdvertisementDto(
                ad.getId(),
                ad.getInstitution().getId(),
                ad.getInstitution().getName(),
                ad.getType(),
                ad.getTitle(),
                ad.getDescription(),
                ad.getBannerImageUrl(),
                ad.getEndDateTime()
        );
    }
}

