package com.caring.caringbackend.domain.recommendation.service;

import com.caring.caringbackend.api.recommendation.dto.request.RecommendRequestDto;
import com.caring.caringbackend.api.recommendation.dto.response.RecommendationResponseDto;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.integration.ai.service.AiServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.caring.caringbackend.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final AiServerService aiServerService;
    private final MemberRepository memberRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;

    // 추천 로직 구현
    @Transactional(readOnly = true)  // Lazy Loading을 위한 트랜잭션 추가
    public RecommendationResponseDto recommendInstitutions(
            Long memberId,
            RecommendRequestDto recommendRequestDto
    ) {
        // AI 서버로 Member와 ElderlyProfile을 보내고
        // 추천 기관 목록을 받아오는 로직을 구현 한다.
        // Member 조회 (@BatchSize로 N+1 방지)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        // ElderlyProfile 조회
        ElderlyProfile elderlyProfile = elderlyProfileRepository.findById(recommendRequestDto.elderlyProfileId())
                .orElseThrow(() -> new BusinessException(ELDERLY_PROFILE_NOT_FOUND));

        // Member의 ElderlyProfile인지 검증
        if (!elderlyProfile.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ELDERLY_PROFILE_ACCESS_DENIED);
        }

        // 추천 기관 목록 받아오기
        RecommendationResponseDto response = aiServerService.recommend(member, elderlyProfile);
        if (response == null) {
            throw new BusinessException(AI_SERVER_COMMUNICATION_FAILED);

        }
        return response;
    }
}
