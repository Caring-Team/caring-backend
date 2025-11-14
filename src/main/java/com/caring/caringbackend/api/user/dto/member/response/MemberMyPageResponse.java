package com.caring.caringbackend.api.user.dto.member.response;

import com.caring.caringbackend.domain.review.entity.Review;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 👤 마이페이지 통합 응답 DTO
 *
 * 회원 기본 정보, 활동 통계, 최근 어르신 프로필/리뷰 요약 정보를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyPageResponse {

    private MemberInfo member;
    private MemberStatisticsResponse statistics;
    private List<ElderlyProfileSummary> elderlyProfiles;
    private List<ReviewSummary> recentReviews;

    public static MemberMyPageResponse of(Member member,
                                          MemberStatisticsResponse statistics,
                                          List<ElderlyProfile> elderlyProfiles,
                                          List<Review> reviews) {
        return MemberMyPageResponse.builder()
                .member(MemberInfo.from(member))
                .statistics(statistics)
                .elderlyProfiles(elderlyProfiles.stream()
                        .map(ElderlyProfileSummary::from)
                        .collect(Collectors.toList()))
                .recentReviews(reviews.stream()
                        .map(ReviewSummary::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 👤 회원 기본 정보 요약
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private LocalDateTime joinedAt;

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .phoneNumber(member.getPhoneNumber())
                    .birthDate(member.getBirthDate())
                    .joinedAt(member.getCreatedAt())
                    .build();
        }
    }

    /**
     * 👵 어르신 프로필 요약
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElderlyProfileSummary {
        private Long id;
        private String name;
        private LocalDate birthDate;
        private String longTermCareGrade;

        public static ElderlyProfileSummary from(ElderlyProfile profile) {
            return ElderlyProfileSummary.builder()
                    .id(profile.getId())
                    .name(profile.getName())
                    .birthDate(profile.getBirthDate())
                    .longTermCareGrade(profile.getLongTermCareGrade() != null
                            ? profile.getLongTermCareGrade().name()
                            : null)
                    .build();
        }
    }

    /**
     * ⭐ 리뷰 요약
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummary {
        private Long id;
        private Long institutionId;
        private String institutionName;
        private int rating;
        private String content;
        private LocalDateTime createdAt;

        public static ReviewSummary from(Review review) {
            return ReviewSummary.builder()
                    .id(review.getId())
                    .institutionId(review.getInstitution() != null ? review.getInstitution().getId() : null)
                    .institutionName(review.getInstitution() != null ? review.getInstitution().getName() : null)
                    .rating(review.getRating())
                    .content(review.getContent())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }
}

