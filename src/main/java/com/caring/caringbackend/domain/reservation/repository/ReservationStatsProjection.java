package com.caring.caringbackend.domain.reservation.repository;

public interface ReservationStatsProjection {
    Long getPendingCount();
    Long getTodayConfirmedCount();
    Long getTodayCancelledCount();
}
