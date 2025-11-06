package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "기관 상담 예약 가능 시간 응답 DTO")
public record InstitutionCounselDetailResponseDto(
        @Schema(description = "상담 서비스 ID", example = "1")
        Long counselId,

        @Schema(description = "서비스 날짜", example = "2025-11-11")
        LocalDate serviceDate,

        @Schema(description = "전체 시간대 정보 (30분 단위, 0시~23시30분, isAvailable로 예약 가능 여부 확인)")
        List<TimeSlotDto> timeSlots
) {
    /**
     * Entity -> DTO 변환
     * 비트마스크(이진수 문자열)를 사람이 읽을 수 있는 시간대 리스트로 변환
     */
    public static InstitutionCounselDetailResponseDto from(InstitutionCounselDetail counselDetail) {
        List<TimeSlotDto> timeSlots = new ArrayList<>();

        // 이진수 문자열 → Long 변환
        long bitmask = counselDetail.getTimeSlotsBitmaskAsLong();

        // 48개 슬롯 (0시~23시30분, 30분 단위)
        for (int slotIndex = 0; slotIndex < 48; slotIndex++) {
            LocalTime startTime = LocalTime.of(slotIndex / 2, (slotIndex % 2) * 30);
            LocalTime endTime = startTime.plusMinutes(30);
            boolean isAvailable = (bitmask & (1L << slotIndex)) != 0;

            TimeSlotDto slot = new TimeSlotDto(
                    slotIndex,
                    startTime,
                    endTime,
                    isAvailable
            );

            timeSlots.add(slot);
        }

        return new InstitutionCounselDetailResponseDto(
                counselDetail.getInstitutionCounsel().getId(),
                counselDetail.getServiceDate(),
                timeSlots
        );
    }
}

