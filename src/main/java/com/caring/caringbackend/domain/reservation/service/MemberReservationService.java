package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.internal.reservation.dto.request.MemberReservationCreateRequestDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.MemberReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.MemberReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberReservationService {
    void createMemberReservation(Long memberId, MemberReservationCreateRequestDto requestDto);

    Page<MemberReservationResponseDto> getMyReservations(Long memberId, Pageable pageable);

    MemberReservationDetailResponseDto getMyReservationDetail(Long memberId, Long reservationId);

    void cancelMyReservation(Long memberId, Long reservationId);
}
