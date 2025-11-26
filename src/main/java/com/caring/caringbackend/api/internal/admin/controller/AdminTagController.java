package com.caring.caringbackend.api.internal.admin.controller;

import com.caring.caringbackend.api.internal.admin.dto.request.TagCreateRequest;
import com.caring.caringbackend.api.internal.admin.dto.request.TagUpdateRequest;
import com.caring.caringbackend.api.internal.admin.dto.response.TagResponse;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.service.TagService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 관리자 태그 관리 Controller
 * 시스템 전역에 영향을 주는 태그 관리 기능을 제공하는 REST API 엔드포인트입니다.
 * 관리자 전용 API로, 태그 생성, 수정, 삭제 기능을 포함합니다
 * @author 윤다인
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/api/v1/admin/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "23. Admin Tag", description = "관리자 태그 관리 API | 태그 생성/수정/삭제/활성화")
public class AdminTagController {

    private final TagService tagService;

    /**
     * 태그 생성 (Admin Only)
     */
    @PostMapping
    @Operation(
            summary = "태그 생성 (시스템 관리자 전용)",
            description = """
                    새로운 시스템 태그를 생성합니다. (ADMIN 권한 필요)
                    
                    ⚠️ 시스템 전역에 영향을 주는 API입니다.
                    - code는 Enum name 형식으로 중복 불가
                    - displayOrder는 정렬 순서 (null이면 기본값)
                    - 생성 시 자동으로 활성화 상태로 설정됩니다
                    - 모든 기관, 리뷰, 사용자 선호 태그에 영향을 줍니다
                    """
    )
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @Valid @RequestBody TagCreateRequest request) {
        Tag tag = tagService.createTag(request);
        TagResponse response = TagResponse.from(tag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("태그 생성 성공", response));
    }

    /**
     * 태그 수정 (Admin Only)
     */
    @PutMapping("/{tagId}")
    @Operation(
            summary = "태그 수정 (시스템 관리자 전용)",
            description = """
                    기존 시스템 태그 정보를 수정합니다. (ADMIN 권한 필요)
                    
                    ⚠️ 시스템 전역에 영향을 주는 API입니다.
                    - 카테고리와 코드는 수정 불가
                    - name, description, isActive, displayOrder 수정 가능
                    - null인 필드는 변경되지 않습니다
                    - 이 태그를 사용하는 모든 기관, 리뷰에 영향을 줍니다
                    """
    )
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody TagUpdateRequest request) {
        Tag tag = tagService.updateTag(tagId, request);
        TagResponse response = TagResponse.from(tag);
        return ResponseEntity.ok(ApiResponse.success("태그 수정 성공", response));
    }

    /**
     * 태그 삭제/비활성화 (Admin Only)
     */
    @DeleteMapping("/{tagId}")
    @Operation(
            summary = "태그 비활성화 (시스템 관리자 전용)",
            description = """
                    시스템 태그를 비활성화합니다. (ADMIN 권한 필요)
                    
                    ⚠️ 시스템 전역에 영향을 주는 API입니다.
                    - 실제로 삭제되지 않고 isActive가 false로 변경됩니다 (Soft Delete)
                    - 비활성화된 태그는 조회 API에서 제외됩니다
                    - 기존 연결된 데이터는 유지되지만 더 이상 선택할 수 없습니다
                    - 모든 기관, 리뷰, 사용자 선호 태그에 영향을 줍니다
                    """
    )
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok(ApiResponse.success("태그 비활성화 성공", null));
    }
}
