package com.caring.caringbackend.global.integration.ai.service;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;

/**
 * AI 서버 통신 서비스
 *
 * @author 나의찬
 * @since 2025-11-22
 */
public interface AiServerService {

    /**
     * 기관 데이터를 AI 서버로 전송하여 임베딩 벡터로 저장
     *
     * @param institution 기관 엔티티
     * @return 성공 여부
     */
    boolean sendInstitutionEmbedding(Institution institution);

    /**
     * 기관 임베딩 데이터 삭제 (기관 삭제 시)
     *
     * @param institutionId 기관 ID
     * @return 성공 여부
     */
    boolean deleteInstitutionEmbedding(Long institutionId);
}

