package com.caring.caringbackend.api.institution.dto.response;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselReservationStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "기관 상담 예약 가능 시간 응답 DTO")
public record InstitutionCounselReservationDetailResponseDto(
        @Schema(description = "상담 서비스 ID", example = "1")
        Long counselId,

        @Schema(description = "서비스 날짜", example = "2025-11-11")
        LocalDate serviceDate,

        @Schema(description = "전체 시간대 정보 (30분 단위, 0시~23시30분, isAvailable로 예약 가능 여부 확인)")
        List<TimeSlotDto> timeSlots
) {
    /**
     * Entity -> DTO 변환 비트마스크(이진수 문자열)를 사람이 읽을 수 있는 시간대 리스트로 변환
     */
    public static InstitutionCounselReservationDetailResponseDto from(InstitutionCounselDetail counselDetail) {
        List<TimeSlotDto> timeSlots = new ArrayList<>();

        String timeSlotsBitmask = counselDetail.getTimeSlotsBitmask();

        char[] charArray = timeSlotsBitmask.toCharArray();
        CounselTimeUnit unit = counselDetail.getUnit();

        int slotSpace = unit.getSpace();

        for (int i = 0; i < 48; i += slotSpace) {

            if (charArray[i] == CounselReservationStatus.UNAVAILABLE.getCode()) {
                continue;
            }

            char code = charArray[i];

            String startTime = String.format("%02d:%02d", i / 2, i % 2 * 30);
            String endTime = String.format("%02d:%02d", (i + slotSpace) / 2, (i + slotSpace) % 2 * 30);

            boolean isAvailable = code == CounselReservationStatus.AVAILABLE.getCode();
            TimeSlotDto slot = new TimeSlotDto(startTime, endTime, isAvailable);
            timeSlots.add(slot);
        }

        return new InstitutionCounselReservationDetailResponseDto(
                counselDetail.getInstitutionCounsel().getId(),
                counselDetail.getServiceDate(),
                timeSlots
        );
    }
}

