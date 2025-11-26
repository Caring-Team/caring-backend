package com.caring.caringbackend.api.internal.user.controller;

import com.caring.caringbackend.api.internal.user.dto.member.request.MemberPreferenceTagRequest;
import com.caring.caringbackend.api.internal.user.dto.member.request.MemberUpdateRequest;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberDetailResponse;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberListResponse;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberMyPageResponse;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberResponse;
import com.caring.caringbackend.api.internal.user.dto.member.response.MemberStatisticsResponse;
import com.caring.caringbackend.api.internal.tag.dto.response.TagListResponse;
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
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
@Tag(name = "👤 Member", description = "회원(보호자) 관리 API")
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회 (토큰 기반)
     */
    @GetMapping
    @Operation(summary = "1. 내 회원 정보 조회", description = "인증된 사용자의 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMe(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberResponse member = memberService.getMemberById(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 조회 성공", member));
    }

    /**
     * 내 상세 정보 조회
     */
    @GetMapping("/detail")
    @Operation(summary = "2. 내 회원 상세 조회", description = "인증된 사용자의 회원 정보와 어르신 프로필 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMeDetail(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberDetailResponse memberDetail = memberService.getMemberDetailById(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 성공", memberDetail));
    }

    /**
     * 내 정보 수정 (토큰 기반)
     */
    @PutMapping
    @Operation(summary = "3. 내 정보 수정", description = "인증된 사용자의 회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMe(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody MemberUpdateRequest request) {

        MemberResponse updatedMember = memberService.updateMember(memberDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", updatedMember));
    }

    /**
     * 내 계정 삭제 (토큰 기반)
     */
    @DeleteMapping
    @Operation(summary = "4. 내 계정 삭제", description = "인증된 사용자가 자신의 계정을 소프트 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMe(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        memberService.deleteMember(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("회원 삭제 성공", null));
    }

    /**
     * 내 활동 통계 조회
     */
    @GetMapping("/statistics")
    @Operation(summary = "5. 내 활동 통계 조회", description = "인증된 사용자의 활동 통계를 조회합니다. (등록된 어르신 수, 작성한 리뷰 수, 가입일)")
    public ResponseEntity<ApiResponse<MemberStatisticsResponse>> getMyStatistics(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberStatisticsResponse statistics = memberService.getStatistics(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("활동 통계 조회 성공", statistics));
    }

    /**
     * 내 마이페이지 데이터 조회
     */
    @GetMapping("/mypage")
    @Operation(summary = "6. 마이페이지 조회", description = "인증된 사용자의 마이페이지 통합 데이터를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberMyPageResponse>> getMyPage(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        MemberMyPageResponse myPage = memberService.getMyPage(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("마이페이지 조회 성공", myPage));
    }
    
    /**
     * 내 선호 태그 조회
     */
    @GetMapping("/preference-tags")
    @Operation(summary = "7. 내 선호 태그 조회", description = "인증된 사용자의 선호 태그 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<TagListResponse>> getMyPreferenceTags(
            @AuthenticationPrincipal MemberDetails memberDetails) {

        TagListResponse tags = memberService.getPreferenceTags(memberDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("선호 태그 조회 성공", tags));
    }
    
    /**
     * 내 선호 태그 설정
     */
    @PutMapping("/preference-tags")
    @Operation(summary = "8. 내 선호 태그 설정", description = "인증된 사용자의 선호 태그를 설정합니다. (기존 태그를 덮어씁니다, 최대 10개)")
    public ResponseEntity<ApiResponse<Void>> setMyPreferenceTags(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody MemberPreferenceTagRequest request) {

        memberService.setPreferenceTags(memberDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("선호 태그 설정 성공", null));
    }
}
