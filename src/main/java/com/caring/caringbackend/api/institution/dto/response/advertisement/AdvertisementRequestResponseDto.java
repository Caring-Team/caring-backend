package com.caring.caringbackend.api.institution.dto.response.advertisement;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 광고 신청 응답 DTO (간단 버전)
 */
@Schema(description = "광고 신청 응답")
public record AdvertisementRequestResponseDto(
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

        @Schema(description = "시작 예정 일시")
        LocalDateTime startDateTime,

        @Schema(description = "종료 예정 일시")
        LocalDateTime endDateTime,

        @Schema(description = "신청 일시")
        LocalDateTime createdAt
) {
    /**
     * Entity를 DTO로 변환
     */
    public static AdvertisementRequestResponseDto from(InstitutionAdvertisementRequest request) {
        return new AdvertisementRequestResponseDto(
                request.getId(),
                request.getInstitution().getId(),
                request.getInstitution().getName(),
                request.getType(),
                request.getStatus(),
                request.getTitle(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getCreatedAt()
        );
    }
}

