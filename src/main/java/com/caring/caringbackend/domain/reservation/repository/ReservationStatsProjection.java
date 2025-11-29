package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;

public interface ReservationStatsProjection {
    ReservationStatus getStatus();
    Long getCount();
}
