package com.caring.caringbackend.api.controller;

import com.caring.caringbackend.api.dto.elderly.request.ElderlyProfileCreateRequest;
import com.caring.caringbackend.api.dto.elderly.request.ElderlyProfileUpdateRequest;
import com.caring.caringbackend.api.dto.elderly.response.ElderlyProfileListResponse;
import com.caring.caringbackend.api.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.elderly.service.ElderlyProfileService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 👵 어르신 프로필(ElderlyProfile) 관리 Controller
 * 
 * 회원의 어르신 프로필 CRUD 기능을 제공하는 REST API 엔드포인트입니다.
 * 모든 엔드포인트는 회원 ID를 포함하여 소유자 검증을 수행합니다.
 * 
 * TODO: 인증 기능 적용 시, @PathVariable memberId 대신 @AuthenticationPrincipal MemberDetails 사용 예정
 * @author 윤다인
 * @since 2025-10-28
 */
@RestController
@RequestMapping("/api/v1/members/{memberId}/elderly-profiles")
@RequiredArgsConstructor
@Tag(name = "👵 Elderly Profile", description = "어르신 프로필 관리 API")
public class ElderlyProfileController {

    private final ElderlyProfileService elderlyProfileService;

    /**
     * 어르신 프로필 생성
     * 
     * 특정 회원이 어르신 프로필을 신규 등록합니다.
     * 한 회원은 여러 어르신 프로필을 등록할 수 있습니다.
     */
    @PostMapping
    @Operation(summary = "어르신 프로필 생성", description = "회원이 어르신 프로필을 신규 등록합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> createProfile(
            @PathVariable Long memberId,
            @Valid @RequestBody ElderlyProfileCreateRequest request) {
        
        ElderlyProfileResponse profile = elderlyProfileService.createProfile(memberId, request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("어르신 프로필 등록 성공", profile));
    }

    /**
     * 회원의 어르신 프로필 목록 조회
     * 
     * 특정 회원이 등록한 모든 어르신 프로필을 조회합니다.
     * 삭제된 프로필은 포함되지 않습니다.
     */
    @GetMapping
    @Operation(summary = "어르신 프로필 목록 조회", description = "회원의 모든 어르신 프로필을 조회합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileListResponse>> getProfiles(
            @PathVariable Long memberId) {
        
        ElderlyProfileListResponse profiles = elderlyProfileService.getProfilesByMember(memberId);
        
        return ResponseEntity.ok(
            ApiResponse.success("어르신 프로필 목록 조회 성공", profiles)
        );
    }

    /**
     * 어르신 프로필 단건 조회
     *
     * 특정 어르신 프로필의 기본 정보를 조회합니다.
     * 소유자 검증이 자동으로 수행됩니다.
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "어르신 프로필 조회", description = "특정 어르신 프로필의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> getProfile(
            @PathVariable Long memberId,
            @PathVariable Long profileId) {
        
        ElderlyProfileResponse profile = elderlyProfileService.getProfile(memberId, profileId);
        
        return ResponseEntity.ok(
            ApiResponse.success("어르신 프로필 조회 성공", profile)
        );
    }

    /**
     * 어르신 프로필 수정
     * 
     * 어르신 프로필의 정보를 수정합니다.
     * 소유자 검증이 자동으로 수행됩니다.
     */
    @PutMapping("/{profileId}")
    @Operation(summary = "어르신 프로필 수정", description = "어르신 프로필의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<ElderlyProfileResponse>> updateProfile(
            @PathVariable Long memberId,
            @PathVariable Long profileId,
            @Valid @RequestBody ElderlyProfileUpdateRequest request) {
        
        ElderlyProfileResponse updatedProfile = elderlyProfileService.updateProfile(memberId, profileId, request);
        
        return ResponseEntity.ok(
            ApiResponse.success("어르신 프로필 수정 성공", updatedProfile)
        );
    }

    /**
     * 어르신 프로필 삭제 (소프트 삭제)
     * 
     * 어르신 프로필을 삭제합니다 (실제로는 deleted 플래그만 변경).
     * 삭제된 프로필은 조회되지 않으며, 필요 시 복구 가능합니다.
     */
    @DeleteMapping("/{profileId}")
    @Operation(summary = "어르신 프로필 삭제", description = "어르신 프로필을 삭제(소프트 삭제)합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @PathVariable Long memberId,
            @PathVariable Long profileId) {
        
        elderlyProfileService.deleteProfile(memberId, profileId);
        
        return ResponseEntity.ok(
            ApiResponse.success()
        );
    }
}

