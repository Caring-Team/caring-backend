package com.caring.caringbackend.api.internal.chat.controller;

import com.caring.caringbackend.api.internal.chat.dto.response.ConsultRequestListResponse;
import com.caring.caringbackend.domain.chat.service.ChatService;
import com.caring.caringbackend.domain.institution.counsel.entity.enums.ConsultRequestStatus;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
@Tag(name = "07. 💬 Member Consult Requests", description = "회원 상담 내역 API | 상담 요청 내역 조회")
@SecurityRequirement(name = "bearerAuth")
public class MemberConsultRequestController {

    private final ChatService chatService;

    @GetMapping("/consult-requests")
    @Operation(summary = "1. 내 상담 내역 조회", description = "인증된 사용자의 상담 내역 목록을 조회합니다. (페이징, 상태 필터링 지원)")
    public ResponseEntity<ApiResponse<ConsultRequestListResponse>> getMyConsultRequests(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Parameter(description = "상태 필터 (ACTIVE: 진행 중, CLOSED: 종료됨, null: 전체)")
            @RequestParam(required = false) ConsultRequestStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        ConsultRequestListResponse response = chatService.getMyConsultRequests(
                memberDetails.getId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success("상담 내역 조회 성공", response));
    }
}

