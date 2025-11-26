package com.caring.caringbackend.api.internal.reservation.controller;

import com.caring.caringbackend.api.internal.reservation.dto.request.MemberReservationCreateRequestDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.MemberReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.MemberReservationResponseDto;
import com.caring.caringbackend.domain.reservation.service.MemberReservationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me/reservations")
@RequiredArgsConstructor
@Tag(name = "05. 🧑‍🤝‍🧑 Member Reservation", description = "회원 예약 관리 API | 예약 생성/조회/취소")
public class MemberReservationController {

    private final MemberReservationService memberReservationService;

    /**
     * 회원 상담 예약 생성
     * @param memberDetails 인증된 회원 정보
     * @param requestDto 상담 예약 생성 요청 DTO
     * */
    @PostMapping
    @Operation(summary = "1. 회원 상담 예약 생성", description = "회원이 상담 예약을 생성합니다.", operationId = "1")
    public ApiResponse<Void> createMemberReservation(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody MemberReservationCreateRequestDto requestDto
            ) {
        memberReservationService.createMemberReservation(memberDetails.getId(), requestDto);
        return ApiResponse.success();
    }

    /**
     * 내 예약 목록 조회
     * @param memberDetails 인증된 회원 정보
     * @param pageable 페이징 정보
     * */
    @GetMapping
    @Operation(summary = "2. 내 예약 목록 조회", description = "회원의 예약 목록을 조회합니다. (최신순 정렬)", operationId = "2")
    public ApiResponse<Page<MemberReservationResponseDto>> getMyReservations(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MemberReservationResponseDto> reservations = memberReservationService.getMyReservations(
                memberDetails.getId(),
                pageable
        );
        return ApiResponse.success(reservations);
    }

    /**
     * 내 예약 상세 조회
     * @param memberDetails 인증된 회원 정보
     * @param reservationId 예약 ID
     * */
    @GetMapping("/{reservationId}")
    @Operation(summary = "3. 내 예약 상세 조회", description = "회원의 특정 예약 상세 정보를 조회합니다.", operationId = "3")
    public ApiResponse<MemberReservationDetailResponseDto> getMyReservationDetail(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long reservationId
    ) {
        MemberReservationDetailResponseDto reservation = memberReservationService.getMyReservationDetail(
                memberDetails.getId(),
                reservationId
        );
        return ApiResponse.success(reservation);
    }

    /**
     * 내 예약 취소
     * @param memberDetails 인증된 회원 정보
     * @param reservationId 예약 ID
     * */
    @DeleteMapping("/{reservationId}")
    @Operation(summary = "4. 내 예약 취소", description = "회원의 예약을 취소합니다.", operationId = "4")
    public ApiResponse<Void> cancelMyReservation(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long reservationId
    ) {
        memberReservationService.cancelMyReservation(memberDetails.getId(), reservationId);
        return ApiResponse.success("예약이 취소되었습니다.", null);
    }
}
