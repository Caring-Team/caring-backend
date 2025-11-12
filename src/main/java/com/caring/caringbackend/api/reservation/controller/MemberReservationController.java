package com.caring.caringbackend.api.reservation.controller;

import com.caring.caringbackend.api.reservation.dto.request.MemberReservationCreateRequestDto;
import com.caring.caringbackend.domain.reservation.service.MemberReservationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members/reservations")
@RequiredArgsConstructor
@Tag(name = "🧑‍🤝‍🧑 Member Reservation", description = "회원 예약 관리 API")
public class MemberReservationController {

    private final MemberReservationService memberReservationService;

    // 회원 상담 예약
    @PostMapping
    @Operation(summary = "회원 예약")
    public ApiResponse<Void> createMemberReservation(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody MemberReservationCreateRequestDto requestDto
            ) {
        memberReservationService.createMemberReservation(memberDetails.getId(), requestDto);
        return ApiResponse.success();
    }
}
