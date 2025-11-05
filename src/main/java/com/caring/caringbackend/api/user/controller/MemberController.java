package com.caring.caringbackend.api.user.controller;

import com.caring.caringbackend.api.user.dto.member.request.MemberUpdateRequest;
import com.caring.caringbackend.api.user.dto.member.response.MemberDetailResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberListResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberResponse;
import com.caring.caringbackend.domain.user.guardian.service.MemberService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 👤 회원(Member) 관리 Controller
 * 
 * 보호자 회원의 CRUD 기능을 제공하는 REST API 엔드포인트입니다.
 * 
 * @author 윤다인
 * @since 2025-10-28
 */
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "👤 Member", description = "회원(보호자) 관리 API")
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회 (토큰 기반)
     */
    @GetMapping("/me")
    @Operation(summary = "내 회원 정보 조회", description = "인증된 사용자의 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMe(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberResponse member = memberService.getMemberById(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 조회 성공", member));
    }

    /**
     * 내 상세 정보 조회 (어르신 프로필 포함, 토큰 기반)
     */
    @GetMapping("/me/detail")
    @Operation(summary = "내 회원 상세 조회", description = "인증된 사용자의 회원 정보와 어르신 프로필 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMeDetail(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberDetailResponse memberDetail = memberService.getMemberDetailById(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 성공", memberDetail));
    }

    /**
     * 회원 단건 조회
     * 
     * 특정 회원의 기본 정보를 조회합니다.
     * 어르신 프로필은 포함되지 않습니다.
     */
    @GetMapping("/{memberId}")
    @Operation(summary = "회원 조회", description = "특정 회원의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(
            @PathVariable Long memberId) {
        
        MemberResponse member = memberService.getMemberById(memberId);
        
        return ResponseEntity.ok(
            ApiResponse.success("회원 조회 성공", member)
        );
    }

    /**
     * 회원 상세 조회 (어르신 프로필 포함)
     * 
     * 특정 회원의 상세 정보를 조회합니다.
     * 등록된 모든 어르신 프로필 정보를 포함합니다.
     */
    @GetMapping("/{memberId}/detail")
    @Operation(summary = "회원 상세 조회", description = "회원 정보와 등록된 어르신 프로필 목록을 함께 조회합니다.")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMemberDetail(
            @PathVariable Long memberId) {
        
        MemberDetailResponse memberDetail = memberService.getMemberDetailById(memberId);
        
        return ResponseEntity.ok(
            ApiResponse.success("회원 상세 조회 성공", memberDetail)
        );
    }

    /**
     * 회원 목록 조회 (페이징)
     * 
     * 관리자 전용: 모든 회원의 기본 정보를 페이징 조회합니다.
     * 기본값: 한 페이지당 20개, 생성일시 기준 정렬
     */
    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "모든 회원 목록을 페이징 형태로 조회합니다.")
    public ResponseEntity<ApiResponse<MemberListResponse>> getMembers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        MemberListResponse members = memberService.getMembers(pageable);
        
        return ResponseEntity.ok(
            ApiResponse.success("회원 목록 조회 성공", members)
        );
    }

    /**
     * 회원 정보 수정
     * 
     * 회원의 기본 정보를 수정합니다.
     * 모든 필드가 업데이트되므로 전체 정보를 전달해야 합니다.
     */
    @PutMapping("/{memberId}")
    @Operation(summary = "회원 수정", description = "회원의 기본 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberUpdateRequest request) {
        
        MemberResponse updatedMember = memberService.updateMember(memberId, request);
        
        return ResponseEntity.ok(
            ApiResponse.success("회원 정보 수정 성공", updatedMember)
        );
    }

    /**
     * 내 정보 수정 (토큰 기반)
     */
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "인증된 사용자의 회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMe(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody MemberUpdateRequest request) {

        MemberResponse updatedMember = memberService.updateMember(memberDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", updatedMember));
    }

    /**
     * 회원 삭제 (소프트 삭제)
     * 
     * 회원 정보를 삭제합니다 (실제로는 deleted 플래그만 변경).
     * 삭제된 회원은 조회되지 않으며, 필요 시 복구 가능합니다.
     */
    @DeleteMapping("/{memberId}")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제(소프트 삭제)합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long memberId) {
        
        memberService.deleteMember(memberId);
        
        return ResponseEntity.ok(
            ApiResponse.success()
        );
    }

    /**
     * 내 계정 삭제 (토큰 기반)
     */
    @DeleteMapping("/me")
    @Operation(summary = "내 계정 삭제", description = "인증된 사용자의 회원 정보를 소프트 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMe(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        memberService.deleteMember(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}

