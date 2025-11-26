package com.caring.caringbackend.api.internal.admin.dto.request;

import com.caring.caringbackend.domain.tag.entity.TagCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 생성 요청 DTO
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateRequest {
    
    /**
     * 태그 카테고리
     */
    @NotNull(message = "태그 카테고리는 필수입니다")
    private TagCategory category;
    
    /**
     * 태그 코드 (Enum name)
     */
    @NotBlank(message = "태그 코드는 필수입니다")
    @Size(max = 50, message = "태그 코드는 50자를 초과할 수 없습니다")
    private String code;
    
    /**
     * 태그 이름
     */
    @NotBlank(message = "태그 이름은 필수입니다")
    @Size(max = 50, message = "태그 이름은 50자를 초과할 수 없습니다")
    private String name;
    
    /**
     * 태그 설명
     */
    @Size(max = 200, message = "태그 설명은 200자를 초과할 수 없습니다")
    private String description;
    
    /**
     * 정렬 순서
     */
    private Integer displayOrder;
}

