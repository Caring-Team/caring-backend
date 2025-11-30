package com.caring.caringbackend.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {

    PENDING("예약 대기"),
    CONFIRMED("예약 확정"),
    COMPLETED("예약 완료"),
    CANCELED("예약 취소");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}
