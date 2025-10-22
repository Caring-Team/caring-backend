package com.caring.caringbackend.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {

    PENDING("예약 대기"),
    CANCELLED("예약 취소"),
    COMPLETED("예약 완료");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}
