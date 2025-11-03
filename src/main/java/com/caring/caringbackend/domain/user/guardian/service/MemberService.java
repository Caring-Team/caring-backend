package com.caring.caringbackend.domain.user.guardian.service;

import com.caring.caringbackend.api.user.dto.member.request.MemberUpdateRequest;
import com.caring.caringbackend.api.user.dto.member.response.MemberDetailResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberListResponse;
import com.caring.caringbackend.api.user.dto.member.response.MemberResponse;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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

        // 회원 정보 업데이트 (JPA 변경 감지로 자동 저장)
        member.updateInfo(
            request.getName(),
            request.getPhoneNumber(),
            request.getGender(),
            request.getBirthDate(),
            request.toAddress(),
            request.toGeoPoint()
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
}
