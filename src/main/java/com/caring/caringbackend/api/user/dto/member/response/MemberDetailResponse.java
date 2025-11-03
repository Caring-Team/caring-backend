package com.caring.caringbackend.api.user.dto.member.response;

import com.caring.caringbackend.api.user.dto.elderly.response.ElderlyProfileResponse;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 👤 회원 상세 정보 응답 DTO
 * <p>
 * 회원의 상세 정보와 등록된 어르신 프로필 목록을 포함하는 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponse {

    /**
     * 👤 회원 기본 정보
     */
    private MemberResponse member;

    /**
     * 👵 등록된 어르신 프로필 목록
     */
    private List<ElderlyProfileResponse> elderlyProfiles;

    /**
     * 📊 어르신 프로필 수
     */
    private int elderlyProfileCount;

    /**
     * 📤 Member 엔티티를 MemberDetailResponse로 변환
     * <p>
     * 회원 정보와 등록된 어르신 프로필 목록을 함께 반환합니다.
     */
    public static MemberDetailResponse from(Member member) {
        List<ElderlyProfile> elderlyProfiles = Optional.ofNullable(member.getElderlyProfiles()).orElse(Collections.emptyList());
        
        return MemberDetailResponse.builder()
            .member(MemberResponse.from(member))
            .elderlyProfiles(
                elderlyProfiles.stream()
                    .map(ElderlyProfileResponse::from)
                    .collect(Collectors.toList())
            )
            .elderlyProfileCount(elderlyProfiles.size())
            .build();
    }
}
