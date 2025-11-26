package com.caring.caringbackend.domain.institution.counsel.entity;

import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselReservationStatus;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.CounselTimeUnit;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 기관 상담 서비스 상세 일정 엔티티
 * <p>
 * 기관 상담 서비스의 날짜별 시간대 예약 가능 여부를 관리합니다.
 * 비트마스크를 사용하여 30분 단위로 예약 가능 시간을 효율적으로 저장합니다.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstitutionCounselDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 낙관적 락을 위한 버전 관리
    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_id", nullable = false)
    private InstitutionCounsel institutionCounsel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounselTimeUnit unit;

    // 상담 서비스 제공 날짜
    @Column(nullable = false)
    private LocalDate serviceDate;

    /**
     *
     * 저장 형식: 이진수 문자열 (48자리, 예: "111111111111111111111111111111111111111111111111")
     * - 48비트 사용 (0~23시 30분 단위)
     * - 각 비트: 2 = 예약 불가, 1 = 예약 가능, 0 = 예약됨
     *
     * - "111111111111111111111111111111111111111111111111" = 모든 시간 예약 가능 (48개 1)
     * - "011111111111111111111111111111111111111111111111" = 00:00-00:30만 예약됨
     * - "111111111111111111000000000000001111111111111111" = 09:00-12:30 예약됨
     * - "000000000000000000000000000000000000000000000000" = 모든 시간 예약됨
     *
     * 비트 위치: 왼쪽부터 슬롯 0(00:00), 1(00:30), ..., 47(23:30)
     */
    @Column(nullable = false, length = 48, columnDefinition = "VARCHAR(48)")
    private String timeSlotsBitmask;

    @Builder(access = AccessLevel.PRIVATE)
    public InstitutionCounselDetail(InstitutionCounsel institutionCounsel, CounselTimeUnit unit, LocalDate serviceDate, String timeSlotsBitmask) {
        this.institutionCounsel = institutionCounsel;
        this.unit = unit;
        this.serviceDate = serviceDate;
        this.timeSlotsBitmask = timeSlotsBitmask;
    }

    public static InstitutionCounselDetail create(InstitutionCounsel institutionCounsel,  LocalDate serviceDate, String timeSlotsBitmask) {
        return InstitutionCounselDetail.builder()
                .institutionCounsel(institutionCounsel)
                .unit(institutionCounsel.getUnit())
                .serviceDate(serviceDate)
                .timeSlotsBitmask(timeSlotsBitmask)
                .build();
    }

    /**
     * 비트마스크 업데이트
     * 예약/취소 시 사용
     *
     * @param newBitmask 예약 상태 문자열 (48자리)
     */
    public void updateTimeSlotsBitmask(String newBitmask) {
        this.timeSlotsBitmask = newBitmask;
    }

    /**
     * 특정 슬롯이 예약 가능한지 확인
     *
     * @param slotIndex 슬롯 인덱스 (0~47)
     * @return 예약 가능 여부
     */
    public boolean isSlotAvailable(int slotIndex) {
        validateSlotIndex(slotIndex);

        // 이진수 문자열에서 해당 인덱스의 비트 확인 (1 = 예약 가능)
        return timeSlotsBitmask.charAt(slotIndex) == CounselReservationStatus.AVAILABLE.getCode();
    }

    /**
     * 특정 슬롯을 예약 상태로 변경 (비트를 0으로)
     *
     * @param slotIndex 슬롯 인덱스 (0~47)
     */
    public void markSlotAsReserved(int slotIndex) {
        validateSlotIndex(slotIndex);

        // 문자열의 해당 위치를 '0'으로 변경
        char[] bits = timeSlotsBitmask.toCharArray();
        bits[slotIndex] = CounselReservationStatus.RESERVED.getCode();
        this.timeSlotsBitmask = new String(bits);
    }

    /**
     * 특정 슬롯을 예약 가능 상태로 변경 (비트를 1로)
     *
     * @param slotIndex 슬롯 인덱스 (0~47)
     */
    public void markSlotAsAvailable(int slotIndex) {
        validateSlotIndex(slotIndex);

        // 문자열의 해당 위치를 '1'로 변경
        char[] bits = timeSlotsBitmask.toCharArray();
        bits[slotIndex] = CounselReservationStatus.AVAILABLE.getCode();
        this.timeSlotsBitmask = new String(bits);
    }

    private void validateSlotIndex(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 48) {
            throw new BusinessException(ErrorCode.INVALID_TIME_SLOT);
        }
    }
}
