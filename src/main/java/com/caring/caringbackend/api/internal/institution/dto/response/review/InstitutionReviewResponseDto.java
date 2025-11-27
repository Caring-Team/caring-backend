package com.caring.caringbackend.api.internal.institution.dto.response.review;

import com.caring.caringbackend.domain.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

public record InstitutionReviewResponseDto(
        // 리뷰 id
        Long reviewId,

        // 작성자 이름
        String memberName,

        // 리뷰 별점
        int rating,

        // 리뷰 내용
        String content,

        // 리뷰 태그들 리스트
        List<String> tags,

        // 생성일
        LocalDateTime createdAt,

        // 수정일
        LocalDateTime updatedAt
) {
    public static InstitutionReviewResponseDto from(Review review) {
        return new InstitutionReviewResponseDto(
                review.getId(),
                review.getMember().getName(),
                review.getRating(),
                review.getContent(),
                review.getReviewTags().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
