package com.caring.caringbackend.api.external.controller;

import com.caring.caringbackend.api.internal.admin.dto.response.TagListResponse;
import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import com.caring.caringbackend.domain.tag.service.TagService;
import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "22. 🏷 Public Tag", description = "공개 태그 API | 태그 조회 (인증 불필요)")
public class PublicTagController {

    private final TagService tagService;

    /**
     * 전체 활성화된 태그 목록 조회
     */
    @GetMapping
    @Operation(
            summary = "1. 전체 태그 목록 조회",
            description = "활성화된 모든 태그를 카테고리별로 정렬하여 조회합니다. (공개 API)"
    )
    public ResponseEntity<ApiResponse<TagListResponse>> getAllTags() {
        List<Tag> tags = tagService.getAllActiveTags();
        TagListResponse response = TagListResponse.from(tags);
        return ResponseEntity.ok(ApiResponse.success("전체 태그 목록 조회 성공", response));
    }

    /**
     * 카테고리별 태그 목록 조회
     */
    @GetMapping("/category/{category}")
    @Operation(
            summary = "카테고리별 태그 목록 조회",
            description = """
                    특정 카테고리의 활성화된 태그 목록을 조회합니다. (공개 API)
                    
                    **카테고리 종류:**
                    - SPECIALIZATION: 전문/질환
                    - SERVICE: 서비스 유형
                    - OPERATION: 운영 특성
                    - ENVIRONMENT: 환경/시설
                    - REVIEW: 리뷰 유형
                    """
    )
    public ResponseEntity<ApiResponse<TagListResponse>> getTagsByCategory(
            @PathVariable TagCategory category) {
        List<Tag> tags = tagService.getTagsByCategory(category);
        TagListResponse response = TagListResponse.from(tags);
        return ResponseEntity.ok(
                ApiResponse.success(category.getDescription() + " 태그 목록 조회 성공", response));
    }
}
