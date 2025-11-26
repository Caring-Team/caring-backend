package com.caring.caringbackend.api.internal.Member.dto.review.response;

import com.caring.caringbackend.domain.tag.entity.Tag;
import com.caring.caringbackend.domain.tag.entity.TagCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 태그 정보 DTO
 *
 * @author 윤다인
 * @since 2025-11-18
 */
@Getter
@Builder
@Schema(description = "태그 정보")
public class TagDto {

    @Schema(description = "태그 ID", example = "1")
    private Long id;

    @Schema(description = "태그 코드", example = "DEMENTIA_CARE")
    private String code;

    @Schema(description = "태그 이름", example = "치매 전문")
    private String name;

    @Schema(description = "태그 카테고리", example = "SPECIALIZATION")
    private TagCategory category;

    /**
     * Tag 엔티티를 TagDto로 변환
     */
    public static TagDto from(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .code(tag.getCode())
                .name(tag.getName())
                .category(tag.getCategory())
                .build();
    }
}

