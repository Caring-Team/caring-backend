package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
