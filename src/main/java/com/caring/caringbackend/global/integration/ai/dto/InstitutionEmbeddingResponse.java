package com.caring.caringbackend.global.integration.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 서버로부터 받는 임베딩 응답 DTO
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionEmbeddingResponse {

    /**
     * 기관 ID
     */
    private Long institutionId;

    /**
     * 임베딩 성공 여부
     */
    private Boolean success;

    /**
     * 메시지
     */
    private String message;

    /**
     * 벡터 차원 수 (선택)
     */
    private Integer vectorDimension;
}

