package com.caring.caringbackend.api.institution.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 기관 태그 설정 요청 DTO
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionTagRequest {

    /**
     * 태그 ID 목록 (최대 10개)
     */
    @NotNull(message = "태그 ID 목록은 필수입니다")
    @Size(max = 10, message = "태그는 최대 10개까지 선택할 수 있습니다")
    private List<Long> tagIds;
}

