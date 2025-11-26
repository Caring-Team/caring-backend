package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.internal.reservation.dto.request.MemberReservationCreateRequestDto;

public interface MemberReservationService {
    void createMemberReservation(Long memberId, MemberReservationCreateRequestDto requestDto);
}
