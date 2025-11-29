package com.caring.caringbackend.api.internal.institution.controller;

import com.caring.caringbackend.api.internal.admin.dto.response.TagListResponse;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionCreateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionTagRequest;
import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionUpdateRequestDto;
import com.caring.caringbackend.api.internal.institution.dto.response.DashboardDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import com.caring.caringbackend.domain.reservation.service.InstitutionReservationService;
import com.caring.caringbackend.domain.review.service.ReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 기관 프로필 관련 컨트롤러
 *
 * @author 나의찬
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/institutions/me")
@Tag(name = "10. 🏥 Institution Profile", description = "기관 프로필 관리 API | 내 기관 정보 조회/수정, 태그 관리")
public class InstitutionController {
    private final InstitutionService institutionService;


    /**
     * 기관 등록 요청
     *
     * @param requestDto   기관 생성 요청 DTO
     * @param file         사업자 등록증 이미지 파일 (선택사항)
     * @param adminDetails 인증된 기관 관리자 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "1. 기관 등록 요청", description = "새로운 기관 등록을 요청합니다. (인증 필요)")
    public ApiResponse<Void> registerInstitution(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @Valid @RequestPart(value = "data") InstitutionCreateRequestDto requestDto,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        institutionService.registerInstitution(adminDetails.getId(), requestDto, file);
        return ApiResponse.success(null);
    }

    /**
     * 내 기관 정보 조회
     */
    @GetMapping
    @Operation(summary = "2. 내 기관 정보 조회", description = "인증된 기관 관리자의 소속 기관 정보를 조회합니다. (인증 필요)")
    public ApiResponse<InstitutionDetailResponseDto> getMyInstitution(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        InstitutionDetailResponseDto institutionDetail = institutionService.getMyInstitution(adminDetails.getId());
        return ApiResponse.success(institutionDetail);
    }

    /**
     * 3. 내 기관 예약 상태별 개수 조회
     * 확정 대기, 오늘 확정, 오늘 취소 개수, 신규 리뷰 개수 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "3. 내 기관 예약 상태별 개수 및 신규 리뷰 개수 조회", description = "인증된 기관 관리자의 소속 기관의 예약 상태별 개수 및 신규 리뷰 개수를 조회합니다. (인증 필요)")
    public ApiResponse<DashboardDto> getMyInstitutionDashboard(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        return null;
    }

    /**
     * 기관 정보 수정
     *
     * @param adminDetails                인증된 기관 관리자 정보
     * @param institutionUpdateRequestDto 기관 수정 요청 DTO
     *
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "4. 기관 정보 수정", description = "기관의 정보를 수정합니다. (OWNER 권한 필요)")
    public ApiResponse<Void> updateInstitution(
            @RequestPart(value = "file", required = false) MultipartFile mainImage,
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @Valid @RequestPart InstitutionUpdateRequestDto institutionUpdateRequestDto
    ) {
        institutionService.updateInstitution(adminDetails.getId(), institutionUpdateRequestDto, mainImage);
        return ApiResponse.success(null);
    }


    /**
     * 기관 입소 가능 여부 변경
     *
     * @param adminDetails         인증된 기관 관리자 정보
     * @param isAdmissionAvailable 입소 가능 여부
     */
    @PatchMapping("/admission-availability")
    @Operation(summary = "5. 기관 입소 가능 여부 변경", description = "기관의 입소 가능 여부를 변경합니다. (OWNER/STAFF 권한 필요)")
    public ApiResponse<Void> changeAdmissionAvailability(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @RequestParam Boolean isAdmissionAvailable
    ) {
        institutionService.changeAdmissionAvailability(adminDetails.getId(), isAdmissionAvailable);
        return ApiResponse.success(null);
    }


    /**
     * 적용중인 기관 테그 조회
     *
     * @param adminDetails  인증된 기관 관리자 정보
     */
    @GetMapping("/tags")
    public ApiResponse<TagListResponse> getInstitutionTags(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {

        return ApiResponse.success(institutionService.getInstitutionTags(adminDetails.getId()));
    }

    /**
     * 적용중인 기관 테그를 모든 Active 상태의 테그와 함께 조회
     *
     * @param adminDetails  인증된 기관 관리자 정보
     */
    @GetMapping("/tags/all")
    public ApiResponse<TagListResponse> getInstitutionTagsAll(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {

        return ApiResponse.success(institutionService.getInstitutionTagsWithAllActivateTags(adminDetails.getId()));
    }

    /**
     * 기관 태그 수정
     *
     * @param adminDetails  인증된 기관 관리자 정보
     * @param request       태그 설정 요청 DTO
     */
    @PutMapping("/tags")
    @Operation(summary = "5. 기관 태그 설정", description = "기관의 태그를 수정합니다. (기존 태그를 덮어씁니다, 최대 10개, OWNER/STAFF 권한 필요)")
    public ApiResponse<Void> setInstitutionTags(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @Valid @RequestBody InstitutionTagRequest request
    ) {
        institutionService.setInstitutionTags(adminDetails.getId(), request.getTagIds());
        return ApiResponse.success(null);
    }

    /**
     * 기관 삭제 (Soft Delete)
     *
     * @param adminDetails  인증된 기관 관리자 정보
     */
    @DeleteMapping
    @Operation(summary = "6. 기관 삭제", description = "기관을 논리적으로 삭제합니다. 입소 가능 여부가 자동으로 false로 변경됩니다. (OWNER 권한 필요)")
    public ApiResponse<Void> deleteInstitution(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails
    ) {
        institutionService.deleteInstitution(adminDetails.getId());
        return ApiResponse.success(null);
    }
}
