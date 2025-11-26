package com.caring.caringbackend.api.internal.admin.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 수정 요청 DTO
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateRequest {
    
    /**
     * 태그 이름
     */
    @Size(max = 50, message = "태그 이름은 50자를 초과할 수 없습니다")
    private String name;
    
    /**
     * 태그 설명
     */
    @Size(max = 200, message = "태그 설명은 200자를 초과할 수 없습니다")
    private String description;
    
    /**
     * 활성화 여부
     */
    private Boolean isActive;
    
    /**
     * 정렬 순서
     */
    private Integer displayOrder;
}

