package com.caring.caringbackend.domain.user.elderly.service;

import com.caring.caringbackend.api.user.dto.elderly.request.ElderlyProfileCreateRequest;
import com.caring.caringbackend.api.user.dto.elderly.request.ElderlyProfileUpdateRequest;
import com.caring.caringbackend.api.user.dto.elderly.response.ElderlyProfileListResponse;
import com.caring.caringbackend.api.user.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.ElderlyProfileNotFoundException;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 어르신 프로필(ElderlyProfile) 비즈니스 로직을 처리하는 서비스
 * 
 * @author 윤다인
 * @since 2025-10-28
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderlyProfileService {

    private final ElderlyProfileRepository elderlyProfileRepository;
    private final MemberRepository memberRepository;

    /**
     * 어르신 프로필 등록
     */
    @Transactional
    public ElderlyProfileResponse createProfile(Long memberId, ElderlyProfileCreateRequest request) {
        // 1. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. ElderlyProfile 엔티티 생성
        ElderlyProfile profile = ElderlyProfile.builder()
                .member(member)
                .name(request.getName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .bloodType(request.getBloodType())
                .phoneNumber(request.getPhoneNumber())
                .activityLevel(request.getActivityLevel())
                .cognitiveLevel(request.getCognitiveLevel())
                .notes(request.getNotes())
                .address(request.toAddress())
                .location(request.toGeoPoint())
                .build();

        // 3. DB 저장
        ElderlyProfile savedProfile = elderlyProfileRepository.save(profile);

        // 4. 응답 반환
        return ElderlyProfileResponse.from(savedProfile);
    }

    /**
     * 회원의 어르신 프로필 목록 조회
     */
    public ElderlyProfileListResponse getProfilesByMember(Long memberId) {
        // 1. 회원 존재 확인
        memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 어르신 프로필 목록 조회 (삭제되지 않은 프로필만)
        List<ElderlyProfile> profiles = elderlyProfileRepository.findByMemberIdAndDeletedFalse(memberId);

        // 3. DTO 변환 및 반환
        List<ElderlyProfileResponse> profileResponses = profiles.stream()
                .map(ElderlyProfileResponse::from)
                .collect(Collectors.toList());

        return ElderlyProfileListResponse.of(profileResponses);
    }

    /**
     * 특정 어르신 프로필 조회
     */
    public ElderlyProfileResponse getProfile(Long memberId, Long profileId) {
        // 어르신 프로필의 보호자 접근 검증 포함 조회 (삭제되지 않은 프로필만)
        ElderlyProfile profile = elderlyProfileRepository.findByIdAndMemberIdAndDeletedFalse(profileId, memberId)
                .orElseThrow(() -> new ElderlyProfileNotFoundException(profileId));

        return ElderlyProfileResponse.from(profile);
    }

    /**
     * 어르신 프로필 수정
     */
    @Transactional
    public ElderlyProfileResponse updateProfile(Long memberId, Long profileId, 
                                                  ElderlyProfileUpdateRequest request) {
        // 1. 프로필 조회 (어르신 프로필의 보호자 접근 검증, 삭제되지 않은 프로필만)
        ElderlyProfile profile = elderlyProfileRepository.findByIdAndMemberIdAndDeletedFalse(profileId, memberId)
                .orElseThrow(() -> new ElderlyProfileNotFoundException(profileId));

        // 2. 프로필 정보 업데이트 (JPA 변경 감지로 자동 저장)
        profile.updateInfo(
            request.getName(),
            request.getGender(),
            request.getBirthDate(),
            request.getBloodType(),
            request.getPhoneNumber(),
            request.getActivityLevel(),
            request.getCognitiveLevel(),
            request.getNotes(),
            request.toAddress(),
            request.toGeoPoint()
        );
        
        // 3. 응답 반환
        return ElderlyProfileResponse.from(profile);
    }

    /**
     * 어르신 프로필 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteProfile(Long memberId, Long profileId) {
        // 1. 프로필 조회 (어르신 프로필의 보호자 접근 검증, 삭제되지 않은 프로필만)
        ElderlyProfile profile = elderlyProfileRepository.findByIdAndMemberIdAndDeletedFalse(profileId, memberId)
                .orElseThrow(() -> new ElderlyProfileNotFoundException(profileId));

        // 2. 소프트 삭제
        profile.softDelete();
    }
}

