package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "기관 상담 예약 가능 시간 응답 DTO")
public record InstitutionCounselDetailResponseDto(

        Long counselId,

        @Schema(description = "서비스 날짜", example = "2025-11-11")
        LocalDate serviceDate,

        @Schema(description = "예약 가능한 시간대 목록")
        List<TimeSlotDto> availableTimeSlots,

        @Schema(description = "전체 시간대 정보 (30분 단위, 0시~23시30분)")
        List<TimeSlotDto> allTimeSlots
) {
    /**
     * 비트마스크를 사람이 읽을 수 있는 시간대 리스트로 변환
     */
    public static InstitutionCounselDetailResponseDto from(InstitutionCounselDetail counselDetail) {
        List<TimeSlotDto> allSlots = new ArrayList<>();
        List<TimeSlotDto> availableSlots = new ArrayList<>();

        long bitmask = counselDetail.getTimeSlotsBitmask();

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

            allSlots.add(slot);
            if (isAvailable) {
                availableSlots.add(slot);
            }
        }

        return new InstitutionCounselDetailResponseDto(
                counselDetail.getInstitutionCounsel().getId(),
                counselDetail.getServiceDate(),
                availableSlots,
                allSlots
        );
    }
}

