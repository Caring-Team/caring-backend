package com.caring.caringbackend.api.institution.controller;

import com.caring.caringbackend.api.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.api.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.api.institution.dto.response.InstitutionProfileResponseDto;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
@Tag(name = "🏥 Institution Profile", description = "기관 프로필 관리 API")
public class InstitutionController {
    private final InstitutionService institutionService;

    /**
     * 기관 등록 요청
     *
     * @param institutionCreateRequestDto 기관 생성 요청 DTO
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
     * 기관 목록 조회 (검색, 필터링, 페이징, 정렬)
     */
    @GetMapping
    @Operation(
            summary = "기관 목록 조회",
            description = """
                    기관 목록을 조회합니다.
                    
                    ### 지원 기능
                    - **페이징**: page (0부터 시작), size (기본 20)
                    - **정렬**: sort (예: sort=name,asc)
                    - **검색**: 이름, 도시, 기관 유형
                    - **필터링**: 승인 상태, 입소 가능 여부, 가격 범위, 병상 수
                    - **거리 기반 검색**: 위도/경도/반경 (km)
                    
                    ### 요청 예시
                    ```
                    GET /api/v1/institutions/profile?page=0&size=10
                    GET /api/v1/institutions/profile?name=서울&city=강남구
                    GET /api/v1/institutions/profile?latitude=37.5665&longitude=126.9780&radiusKm=5.0
                    ```
                    """
    )
    public ResponseEntity<Page<InstitutionProfileResponseDto>> getInstitutions(
            @ParameterObject @PageableDefault(size = 20, page = 0)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @ParameterObject @ModelAttribute InstitutionSearchFilter filter
    ) {
        Page<InstitutionProfileResponseDto> institutions = institutionService.getInstitutions(pageable, filter);
        return ResponseEntity.ok(institutions);
    }


    /**
     * 기관 상세 조회
     *
     * @param institutionId 기관 ID
     * @return 기관 상세 응답 DTO
     */
    @GetMapping("/{institutionId}")
    @Operation(summary = "기관 상세 조회", description = "기관의 상세 정보를 조회합니다.")
    public ResponseEntity<InstitutionDetailResponseDto> getInstitutionDetail(
            @PathVariable Long institutionId
    ) {
        InstitutionDetailResponseDto institutionDetail = institutionService.getInstitutionDetail(institutionId);
        return ResponseEntity.ok().body(institutionDetail);
    }


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
     * 기관 승인 처리 (관리자 전용)
     */
    @PatchMapping("/{institutionId}/approval")
    @Operation(summary = "기관 승인", description = "관리자가 기관 등록 요청을 승인합니다.")
    public ResponseEntity<Void> approveInstitution(
            @PathVariable Long institutionId
    ) {
        // TODO: 관리자 권한 체크
        institutionService.approveInstitution(institutionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 기관 삭제 (Soft Delete)
     */
    @DeleteMapping("/{institutionId}")
    @Operation(summary = "기관 삭제", description = "기관을 논리적으로 삭제합니다. 입소 가능 여부가 자동으로 false로 변경됩니다.")
    public ResponseEntity<Void> deleteInstitution(
            @PathVariable Long institutionId
    ) {
        // TODO: OWNER 또는 관리자 권한 체크

        institutionService.deleteInstitution(institutionId);
        return ResponseEntity.ok().build();
    }
}
