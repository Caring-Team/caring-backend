package com.caring.caringbackend.api.dto.elderly.response;

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
 * 👵 어르신 프로필 목록 응답 DTO
 * <p>
 * 특정 회원의 어르신 프로필 목록을 조회할 때 사용하는 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElderlyProfileListResponse {

    /**
     * 👵 어르신 프로필 목록
     */
    private List<ElderlyProfileResponse> profiles;

    /**
     * 📊 전체 프로필 수
     */
    private int totalCount;

    /**
     * List<ElderlyProfile>을 ElderlyProfileListResponse로 변환
     */
    public static ElderlyProfileListResponse from(List<ElderlyProfile> profiles) {
        List<ElderlyProfile> safeProfiles = Optional.ofNullable(profiles).orElse(Collections.emptyList());
        
        return ElderlyProfileListResponse.builder()
            .profiles(
                safeProfiles.stream()
                    .map(ElderlyProfileResponse::from)
                    .collect(Collectors.toList())
            )
            .totalCount(safeProfiles.size())
            .build();
    }

    /**
     * List<ElderlyProfileResponse>를 받아서 ElderlyProfileListResponse로 변환
     */
    public static ElderlyProfileListResponse of(List<ElderlyProfileResponse> profiles) {
        List<ElderlyProfileResponse> safeProfiles = Optional.ofNullable(profiles).orElse(Collections.emptyList());
        
        return ElderlyProfileListResponse.builder()
            .profiles(safeProfiles)
            .totalCount(safeProfiles.size())
            .build();
    }
}

