package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 기관 입소 가능 여부 변경
     */


    /**
     * 기관 삭제
     */


    /**
     * 기관 승인 처리 (관리자 전용)
     */


}
