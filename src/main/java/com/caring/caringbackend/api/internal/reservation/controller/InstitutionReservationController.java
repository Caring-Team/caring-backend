package com.caring.caringbackend.api.internal.reservation.controller;

import com.caring.caringbackend.api.internal.reservation.dto.request.InstitutionReservationSearchRequestDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.reservation.dto.response.InstitutionReservationResponseDto;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.service.InstitutionReservationService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/institutions/me/reservations")
@RequiredArgsConstructor
@Tag(name = "14. 🏥 Institution Reservation", description = "기관 예약 관리 API | 예약 조회/상태 변경")
public class InstitutionReservationController {

    private final InstitutionReservationService institutionReservationService;

    @GetMapping
    @Operation(summary = "1. 내 기관 예약 목록 조회", description = "내 기관에 대한 예약 목록을 조회합니다.")
    public ApiResponse<Page<InstitutionReservationResponseDto>> getMyInstitutionReservations(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @ParameterObject InstitutionReservationSearchRequestDto searchRequest
    ) {
        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<InstitutionReservationResponseDto> reservations = institutionReservationService.getMyInstitutionReservations(
                adminDetails.getId(),
                searchRequest.getStatus(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                pageable
        );

        return ApiResponse.success(reservations);
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "3. 내 기관 예약 상세 조회", description = "내 기관의 특정 예약 상세 정보를 조회합니다.")
    public ApiResponse<InstitutionReservationDetailResponseDto> getMyInstitutionReservationDetail(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,

            @Parameter(description = "예약 ID", example = "1")
            @PathVariable Long reservationId
    ) {
        InstitutionReservationDetailResponseDto response = institutionReservationService
                .getMyInstitutionReservationDetail(adminDetails.getId(), reservationId);

        return ApiResponse.success(response);
    }

    @PatchMapping("/{reservationId}/status")
    @Operation(summary = "4. 내 기관 예약 상태 변경", description = "내 기관의 특정 예약 상태를 변경합니다.")
    public ApiResponse<InstitutionReservationDetailResponseDto> updateMyInstitutionReservationStatus(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,

            @Parameter(description = "예약 ID", example = "1")
            @PathVariable Long reservationId,

            @Parameter(description = "변경할 상태", example = "COMPLETED")
            @RequestParam ReservationStatus status
    ) {
        InstitutionReservationDetailResponseDto response = institutionReservationService
                .updateMyInstitutionReservationStatus(adminDetails.getId(), reservationId, status);

        return ApiResponse.success(response);
    }
}
