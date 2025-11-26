package com.caring.caringbackend.global.integration.ai.service;

import com.caring.caringbackend.api.internal.Member.dto.recommendation.response.RecommendationResponseDto;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.guardian.entity.Member;

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

    /**
     * AI 서버로 Member와 ElderlyProfile을 보내고 추천 기관 목록을 받아오는 메서드
     *
     * @param member         보호자 회원 엔티티
     * @param elderlyProfile 노인 프로필 엔티티
     * @return 추천 기관 응답 DTO
     */
    RecommendationResponseDto recommend(Member member, ElderlyProfile elderlyProfile);
}

