package com.caring.caringbackend.api.internal.tag.dto.response;

import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 응답 DTO
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {

    /**
     * 태그 ID
     */
    private Long id;

    /**
     * 태그 카테고리
     */
    private TagCategory category;

    /**
     * 태그 카테고리 설명
     */
    private String categoryDescription;

    /**
     * 태그 코드
     */
    private String code;

    /**
     * 태그 이름
     */
    private String name;

    /**
     * 태그 설명
     */
    private String description;

    /**
     * 활성화 여부
     */
    private Boolean isActive;

    /**
     * 정렬 순서
     */
    private Integer displayOrder;

    /**
     * Tag 엔티티를 TagResponse로 변환
     */
    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .category(tag.getCategory())
                .categoryDescription(tag.getCategory().getDescription())
                .code(tag.getCode())
                .name(tag.getName())
                .description(tag.getDescription())
                .isActive(tag.getIsActive())
                .displayOrder(tag.getDisplayOrder())
                .build();
    }
}

