package com.caring.caringbackend.api.internal.Member.controller;

import com.caring.caringbackend.api.internal.Member.dto.elderly.request.ElderlyProfileCreateRequest;
import com.caring.caringbackend.api.internal.Member.dto.elderly.request.ElderlyProfileUpdateRequest;
import com.caring.caringbackend.api.internal.Member.dto.elderly.response.ElderlyProfileListResponse;
import com.caring.caringbackend.api.internal.Member.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.elderly.service.ElderlyProfileService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 👵 어르신 프로필(ElderlyProfile) 관리 Controller
 * 
 * 회원의 어르신 프로필 CRUD 기능을 제공하는 REST API 엔드포인트입니다.
 * 모든 엔드포인트는 인증된 회원(@AuthenticationPrincipal) 기반으로 동작합니다.
 * @author 윤다인
 * @since 2025-10-28
 */
@RestController
@RequestMapping("/api/v1/members/me/elderly-profiles")
@RequiredArgsConstructor
@Tag(name = "04. 👵 Elderly Profile", description = "어르신 프로필 관리 API | 어르신 등록/수정/삭제, 케어 정보 관리")
public class ElderlyProfileController {

    private final ElderlyProfileService elderlyProfileService;

    /**
     * 어르신 프로필 생성 (인증 사용자)
     */
    @PostMapping
    @Operation(summary = "1. 내 어르신 프로필 생성", description = "인증된 사용자가 자신의 어르신 프로필을 신규 등록합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> createProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ElderlyProfileCreateRequest request) {

        ElderlyProfileResponse profile = elderlyProfileService.createProfile(memberDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("어르신 프로필 등록 성공", profile));
    }

    /**
     * 내 어르신 프로필 목록 조회
     */
    @GetMapping
    @Operation(summary = "2. 내 어르신 프로필 목록 조회", description = "인증된 사용자의 모든 어르신 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileListResponse>> getProfiles(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        ElderlyProfileListResponse profiles = elderlyProfileService.getProfilesByMember(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("어르신 프로필 목록 조회 성공", profiles));
    }

    /**
     * 내 어르신 프로필 단건 조회
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "3. 내 어르신 프로필 조회", description = "인증된 사용자의 특정 어르신 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> getProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long profileId) {

        ElderlyProfileResponse profile = elderlyProfileService.getProfile(memberDetails.getId(), profileId);
        return ResponseEntity.ok(ApiResponse.success("어르신 프로필 조회 성공", profile));
    }

    /**
     * 내 어르신 프로필 수정
     */
    @PutMapping("/{profileId}")
    @Operation(summary = "4. 내 어르신 프로필 수정", description = "인증된 사용자의 특정 어르신 프로필을 수정합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> updateProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long profileId,
            @Valid @RequestBody ElderlyProfileUpdateRequest request) {

        ElderlyProfileResponse updated = elderlyProfileService.updateProfile(memberDetails.getId(), profileId, request);
        return ResponseEntity.ok(ApiResponse.success("어르신 프로필 수정 성공", updated));
    }

    /**
     * 내 어르신 프로필 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{profileId}")
    @Operation(summary = "5. 내 어르신 프로필 삭제", description = "인증된 사용자의 특정 어르신 프로필을 삭제합니다(소프트 삭제).")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long profileId) {

        elderlyProfileService.deleteProfile(memberDetails.getId(), profileId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

