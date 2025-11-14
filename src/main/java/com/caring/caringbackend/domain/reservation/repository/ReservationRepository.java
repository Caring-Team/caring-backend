package com.caring.caringbackend.domain.reservation.repository;

import com.caring.caringbackend.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
    // Custom 메서드는 ReservationRepositoryCustom에 정의됨
}
