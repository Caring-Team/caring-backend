package com.caring.caringbackend.domain.recommendation.service;

import com.caring.caringbackend.api.recommendation.dto.request.RecommendRequestDto;
import com.caring.caringbackend.api.recommendation.dto.response.RecommendationResponseDto;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    // 추천 로직 구현
    public RecommendationResponseDto recommendInstitutions(
            Long memberId,
            RecommendRequestDto recommendRequestDto
    ) {
        // AI 서버로 memberId, recommendRequestDto 데이터를 보내고
        // 추천 기관 목록을 받아오는 로직을 구현합니다.

        // 추천의 결과를 테이블에 저장 한다.

        // 추천 결과 반환
        return null;
    }
}
