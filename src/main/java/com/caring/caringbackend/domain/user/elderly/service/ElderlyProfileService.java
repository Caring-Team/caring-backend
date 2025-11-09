package com.caring.caringbackend.domain.user.elderly.service;

import com.caring.caringbackend.api.user.dto.elderly.request.ElderlyProfileCreateRequest;
import com.caring.caringbackend.api.user.dto.elderly.request.ElderlyProfileUpdateRequest;
import com.caring.caringbackend.api.user.dto.elderly.response.ElderlyProfileListResponse;
import com.caring.caringbackend.api.user.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.elderly.entity.ActivityLevel;
import com.caring.caringbackend.domain.user.elderly.entity.CognitiveLevel;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.entity.LongTermCareGrade;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ElderlyProfileNotFoundException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderlyProfileService {

    private final ElderlyProfileRepository elderlyProfileRepository;
    private final MemberRepository memberRepository;
    private final GeocodingService geocodingService;

    /**
     * 어르신 프로필 등록
     */
    @Transactional
    public ElderlyProfileResponse createProfile(Long memberId, ElderlyProfileCreateRequest request) {
        // 1. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 장기요양등급 기본값 처리 (null이면 NONE으로 설정)
        LongTermCareGrade longTermCareGrade = request.getLongTermCareGrade();
        if (longTermCareGrade == null) {
            longTermCareGrade = LongTermCareGrade.NONE;
        }

        // 3. 장기요양등급 검증
        validateLongTermCareGrade(
                longTermCareGrade,
                request.getActivityLevel(),
                request.getCognitiveLevel()
        );

        // 4. 주소 → 위경도 변환 (주소 필수이므로 null 체크 불필요)
        Address address = request.toAddress();
        GeoPoint location = geocodingService.convertAddressToGeoPoint(address);
        log.info("어르신 프로필 생성 시 위치 자동 계산: memberId={}, profileName={}, location={}", 
                memberId, request.getName(), location);

        // 5. ElderlyProfile 엔티티 생성
        ElderlyProfile profile = ElderlyProfile.builder()
                .member(member)
                .name(request.getName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .bloodType(request.getBloodType())
                .phoneNumber(request.getPhoneNumber())
                .activityLevel(request.getActivityLevel())
                .cognitiveLevel(request.getCognitiveLevel())
                .longTermCareGrade(longTermCareGrade)
                .notes(request.getNotes())
                .address(address)
                .location(location)
                .build();

        // 6. DB 저장
        ElderlyProfile savedProfile = elderlyProfileRepository.save(profile);

        // 7. 응답 반환
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

        // 2. 장기요양등급 기본값 처리 (null이면 NONE으로 설정, 수정 시 기존 값 유지)
        LongTermCareGrade longTermCareGrade = request.getLongTermCareGrade();
        if (longTermCareGrade == null) {
            // 수정 시 기존 값 유지 (기존 값이 null이면 NONE으로 설정)
            longTermCareGrade = profile.getLongTermCareGrade() != null 
                    ? profile.getLongTermCareGrade() 
                    : LongTermCareGrade.NONE;
        }

        // 3. 장기요양등급 검증
        validateLongTermCareGrade(
                longTermCareGrade,
                request.getActivityLevel(),
                request.getCognitiveLevel()
        );

        // 4. 주소 → 위경도 변환 (주소 변경 시에만)
        Address updatedAddress = request.toAddress();
        GeoPoint updatedLocation = calculateUpdatedLocation(updatedAddress, profileId);
        
        // 주소가 변경되지 않은 경우 기존 location 유지
        if (updatedLocation == null) {
            updatedLocation = profile.getLocation();
        }

        // 5. 프로필 정보 업데이트 (JPA 변경 감지로 자동 저장)
        profile.updateInfo(
            request.getName(),
            request.getGender(),
            request.getBirthDate(),
            request.getBloodType(),
            request.getPhoneNumber(),
            request.getActivityLevel(),
            request.getCognitiveLevel(),
            longTermCareGrade,
            request.getNotes(),
            updatedAddress,
            updatedLocation
        );
        
        // 6. 응답 반환
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

    /**
     * 업데이트된 위치(위도/경도) 계산
     * 주소가 변경된 경우 Geocoding API 호출
     *
     * @param updatedAddress 업데이트된 Address
     * @param profileId 프로필 ID
     * @return 업데이트된 GeoPoint (변경 없으면 null)
     */
    private GeoPoint calculateUpdatedLocation(Address updatedAddress, Long profileId) {
        if (updatedAddress == null) {
            return null;
        }

        GeoPoint location = geocodingService.convertAddressToGeoPoint(updatedAddress);
        log.info("어르신 프로필 주소 변경으로 인한 위치 업데이트: profileId={}, location={}", profileId, location);
        return location;
    }

    /**
     * 장기요양등급 검증
     * <p>
     * 등급이 있으면 (NONE이 아니면): 인지수준, 활동레벨은 null이어야 함
     * 등급이 없으면 (NONE이면): 인지수준, 활동레벨이 필수
     *
     * @param longTermCareGrade 장기요양등급
     * @param activityLevel 활동 수준
     * @param cognitiveLevel 인지 수준
     */
    private void validateLongTermCareGrade(LongTermCareGrade longTermCareGrade,
                                           ActivityLevel activityLevel,
                                           CognitiveLevel cognitiveLevel) {
        boolean hasGrade = longTermCareGrade != null && longTermCareGrade != LongTermCareGrade.NONE;

        if (hasGrade) {
            // 등급이 있으면 인지수준, 활동레벨은 불필요
            if (activityLevel != null || cognitiveLevel != null) {
                throw new BusinessException(
                        ErrorCode.ELDERLY_PROFILE_INVALID_DATA,
                        "장기요양등급이 있는 경우 인지수준과 활동레벨은 입력할 수 없습니다."
                );
            }
        } else {
            // 등급이 없으면 인지수준, 활동레벨이 필수
            if (activityLevel == null || cognitiveLevel == null) {
                throw new BusinessException(
                        ErrorCode.ELDERLY_PROFILE_INVALID_DATA,
                        "장기요양등급이 없는 경우 인지수준과 활동레벨은 필수입니다."
                );
            }
        }
    }
}

