package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 기관 프로필 관련 컨트롤러
 *
 * @author 나의찬
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/profile")
public class InstitutionController {
    private final InstitutionService institutionService;

    /**
     * 기관 등록 요청
     */
    @PostMapping("/register")
    @Operation(summary = "기관 등록 요청", description = "새로운 기관 등록을 요청합니다.")
    public ResponseEntity<Void> registerInstitution(
            @Valid @RequestBody InstitutionCreateRequestDto institutionCreateRequestDto
    ) {
        institutionService.registerInstitution(institutionCreateRequestDto);
        return ResponseEntity.ok().build();
    }


    /**
     * 목록 조회 (검색, 필터링, 페이징)
     */


    /**
     * 기관 상세 조회
     */



    /**
     * 기관 정보 수정
     */
    @PutMapping("/{institutionId}")
    @Operation(summary = "기관 정보 수정", description = "기관의 정보를 수정합니다.")
    public ResponseEntity<Void> updateInstitution(
            @PathVariable Long institutionId,
            @Valid @RequestBody InstitutionUpdateRequestDto institutionUpdateRequestDto
    ) {
        institutionService.updateInstitution(institutionId, institutionUpdateRequestDto);
        return ResponseEntity.ok().build();
    }


    /**
     * 기관 입소 가능 여부 변경
     */
    @PatchMapping("/{institutionId}/admission-availability")
    @Operation(summary = "기관 입소 가능 여부 변경", description = "기관의 입소 가능 여부를 변경합니다.")
    public ResponseEntity<Void> changeAdmissionAvailability(
            @PathVariable Long institutionId,
            @RequestParam Boolean isAdmissionAvailable
    ) {
        institutionService.changeAdmissionAvailability(institutionId, isAdmissionAvailable);
        return ResponseEntity.ok().build();
    }


    /**
     * 기관 삭제
     */


    /**
     * 기관 승인 처리 (관리자 전용)
     */
    @PutMapping("/{institutionId}/approve")
    @Operation(summary = "기관 승인 처리", description = "관리자가 기관 등록 요청을 승인합니다.")
    public ResponseEntity<Void> approveInstitution(
            @PathVariable Long institutionId
    ) {
        // TODO: 관리자 여부 검증 로직 필요

        // 기관 승인 처리 로직 호출
        institutionService.approveInstitution(institutionId);
        return ResponseEntity.ok().build();
    }


}
