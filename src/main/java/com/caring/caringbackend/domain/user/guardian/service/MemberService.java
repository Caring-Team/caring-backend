package com.caring.caringbackend.domain.user.guardian.service;

import com.caring.caringbackend.api.user.dto.member.request.MemberUpdateRequest;
import com.caring.caringbackend.api.user.dto.member.response.MemberDetailResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberListResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberStatisticsResponse;
import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import com.caring.caringbackend.global.model.Address;
import com.caring.caringbackend.global.model.GeoPoint;
import com.caring.caringbackend.global.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 회원(Member) 비즈니스 로직을 처리하는 서비스
 * 
 * @author 윤다인
 * @since 2025-10-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final GeocodingService geocodingService;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 회원 단건 조회
     */
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        return MemberResponse.from(member);
    }

    /**
     * 회원 상세 조회 (어르신 프로필 포함)
     */
    public MemberDetailResponse getMemberDetailById(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        
        return MemberDetailResponse.from(member);
    }

    /**
     * 회원 목록 조회 (페이징, 삭제되지 않은 회원만)
     */
    public MemberListResponse getMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findByDeletedFalse(pageable);
        
        List<MemberResponse> members = page.getContent().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
        
        return MemberListResponse.of(members, page);
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 주소 → 위경도 변환 (주소 변경 시에만)
        Address updatedAddress = request.toAddress();
        GeoPoint updatedLocation = calculateUpdatedLocation(updatedAddress, memberId);

        // 회원 정보 업데이트 (JPA 변경 감지로 자동 저장)
        member.updateInfo(
            request.getName(),
            request.getPhoneNumber(),
            request.getGender(),
            request.getBirthDate(),
            updatedAddress,
            updatedLocation
        );
        
        return MemberResponse.from(member);
    }

    /**
     * 회원 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.softDelete();
    }

    /**
     * 회원 통계 조회
     * 
     * 등록된 어르신 수, 작성한 리뷰 수, 가입일을 조회합니다.
     */
    public MemberStatisticsResponse getStatistics(Long memberId) {
        // 1. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 2. 등록된 어르신 수 조회 (삭제되지 않은 프로필만)
        long elderlyCount = elderlyProfileRepository.countByMemberIdAndDeletedFalse(memberId);

        // 3. 작성한 리뷰 수 조회 (삭제되지 않은 리뷰만)
        long reviewCount = reviewRepository.countByMemberIdAndDeletedFalse(memberId);

        // 4. 통계 응답 생성
        return MemberStatisticsResponse.builder()
                .elderlyCount(elderlyCount)
                .reviewCount(reviewCount)
                .joinedAt(member.getCreatedAt())
                .build();
    }

    /**
     * 업데이트된 위치(위도/경도) 계산
     * 주소가 변경된 경우 Geocoding API 호출
     *
     * @param updatedAddress 업데이트된 Address
     * @param memberId 회원 ID
     * @return 업데이트된 GeoPoint (변경 없으면 null)
     */
    private GeoPoint calculateUpdatedLocation(Address updatedAddress, Long memberId) {
        if (updatedAddress == null) {
            return null;
        }

        GeoPoint location = geocodingService.convertAddressToGeoPoint(updatedAddress);
        log.info("회원 주소 변경으로 인한 위치 업데이트: memberId={}, location={}", memberId, location);
        return location;
    }
}
