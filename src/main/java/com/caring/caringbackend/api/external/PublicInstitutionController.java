package com.caring.caringbackend.api.external;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionCounselReservationDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionDetailResponseDto;
import com.caring.caringbackend.api.internal.institution.dto.response.InstitutionProfileResponseDto;
import com.caring.caringbackend.api.user.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.domain.institution.counsel.service.InstitutionCounselService;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionService;
import com.caring.caringbackend.domain.review.service.ReviewService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/institutions")
@RequiredArgsConstructor
@Tag(name = "🏥 Public Institution", description = "공개 기관 정보 조회 API")
public class PublicInstitutionController {

    private final InstitutionService institutionService;
    private final InstitutionCounselService institutionCounselService;
    private final ReviewService reviewService;

    /**
     * 기관 목록 조회 (검색, 필터링, 페이징, 정렬)
     */
    @GetMapping
    @Operation(
            summary = "1. 기관 목록 조회",
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
    public ApiResponse<Page<InstitutionProfileResponseDto>> getInstitutions(
            @ParameterObject @PageableDefault(size = 20, page = 0)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @ParameterObject @ModelAttribute InstitutionSearchFilter filter
    ) {
        Page<InstitutionProfileResponseDto> institutions = institutionService.getInstitutions(pageable, filter);
        return ApiResponse.success(institutions);
    }


    /**
     * 기관 상세 조회
     *
     * @param institutionId 기관 ID
     * @return 기관 상세 응답 DTO
     */
    @GetMapping("/{institutionId}")
    @Operation(summary = "2. 기관 상세 조회", description = "기관의 상세 정보를 조회합니다.")
    public ApiResponse<InstitutionDetailResponseDto> getInstitutionDetail(
            @PathVariable Long institutionId
    ) {
        // TODO: 기관 상담 목록 데이터 추가
        InstitutionDetailResponseDto institutionDetail = institutionService.getInstitutionDetail(institutionId);
        return ApiResponse.success(institutionDetail);
    }

    // 상담을 통해 세부 정보를 누를때 detail 동적 생성
    @GetMapping("/{counselId}/details")
    @Operation(summary = "3. 상담 예약 가능 시간 조회", description = "상담 예약 가능 시간을 조회합니다.")
    public ApiResponse<InstitutionCounselReservationDetailResponseDto> getInstitutionCounselDetail(
            @PathVariable Long counselId,
            @RequestParam("date") LocalDate date
    ) {
        InstitutionCounselReservationDetailResponseDto responseDto =
                institutionCounselService.getOrCreateCounselDetail(counselId, date);
        return ApiResponse.success(responseDto);
    }

    /**
     * 기관의 리뷰 목록 조회 (공개)
     *
     * 정렬 옵션:
     * - createdAt,desc (최신순, 기본값)
     * - rating,desc (별점 높은 순)
     * - rating,asc (별점 낮은 순)
     */
    @GetMapping("/{institutionId}/reviews")
    @Operation(
            summary = "4. 기관 리뷰 목록 조회",
            description = "특정 기관의 리뷰 목록을 조회합니다. (공개, 삭제되지 않은 리뷰만)\n\n" +
                    "**정렬 옵션:**\n" +
                    "- `sort=createdAt,desc` (최신순, 기본값)\n" +
                    "- `sort=rating,desc` (별점 높은 순)\n" +
                    "- `sort=rating,asc` (별점 낮은 순)"
    )
    public ResponseEntity<ApiResponse<ReviewListResponse>> getInstitutionReviews(
            @PathVariable Long institutionId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        ReviewListResponse reviews = reviewService.getInstitutionReviews(institutionId, pageable);
        return ResponseEntity.ok(ApiResponse.success("기관 리뷰 목록 조회 성공", reviews));
    }
}
