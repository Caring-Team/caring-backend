package com.caring.caringbackend.api.internal.user.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📊 회원 통계 응답 DTO
 * <p>
 * 회원의 활동 통계 정보를 담는 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatisticsResponse {

    /**
     * 👵 등록된 어르신 수
     */
    private long elderlyCount;

    /**
     * ⭐ 작성한 리뷰 수
     */
    private long reviewCount;

    /**
     * 📅 가입일
     */
    private LocalDateTime joinedAt;
}

