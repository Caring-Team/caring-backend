package com.caring.caringbackend.api.user.dto.review.response;

import com.caring.caringbackend.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ⭐ 리뷰 응답 DTO
 * <p>
 * 리뷰 조회 시 사용하는 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    /**
     * 🔢 리뷰 ID
     */
    private Long id;

    /**
     * 📅 예약 ID
     */
    private Long reservationId;

    /**
     * 👤 작성자 정보
     */
    private MemberInfo member;

    /**
     * 🏥 기관 정보
     */
    private InstitutionInfo institution;

    /**
     * 📝 리뷰 내용
     */
    private String content;

    /**
     * ⭐ 별점
     */
    private int rating;

    /**
     * 🏷️ 리뷰 태그 목록
     */
    private List<TagInfo> tags;

    /**
     * 📅 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 🔄 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * 👤 작성자 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String name;
    }

    /**
     * 🏥 기관 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitutionInfo {
        private Long id;
        private String name;
    }

    /**
     * 🏷️ 태그 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        private Long id;
        private String name;
    }

    /**
     * 📤 Review 엔티티를 ReviewResponse로 변환
     */
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .member(review.getMember() != null ?
                        MemberInfo.builder()
                                .id(review.getMember().getId())
                                .name(review.getMember().getName())
                                .build() : null)
                .institution(review.getInstitution() != null ?
                        InstitutionInfo.builder()
                                .id(review.getInstitution().getId())
                                .name(review.getInstitution().getName())
                                .build() : null)
                .content(review.getContent())
                .rating(review.getRating())
                .tags(extractTags(review))
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * 리뷰에서 태그 정보 추출
     * TODO: ReviewTagMapping 관계 추가 후 구현
     * ReviewTagMapping 추가 시, fetch join 또는 DTO projection 방식으로 변환
     */
    private static List<TagInfo> extractTags(Review review) {
        // TODO: ReviewTagMapping을 통해 태그 정보 추출
        // 현재는 Review 엔티티에 태그 관계가 명시적으로 없으므로 빈 리스트 반환
        return List.of();
    }
}

