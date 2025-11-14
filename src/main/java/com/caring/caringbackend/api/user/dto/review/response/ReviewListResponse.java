package com.caring.caringbackend.api.user.dto.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ⭐ 리뷰 목록 응답 DTO
 * <p>
 * 리뷰 목록 조회 시 사용하는 페이징 정보를 포함한 응답 객체입니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {

    /**
     * ⭐ 리뷰 목록
     */
    private List<ReviewResponse> content;

    /**
     * 📊 전체 리뷰 수
     */
    private long totalElements;

    /**
     * 📄 전체 페이지 수
     */
    private int totalPages;

    /**
     * 📍 현재 페이지 번호 (0부터 시작)
     */
    private int currentPage;

    /**
     * 📏 페이지 크기
     */
    private int pageSize;

    /**
     * 🔚 마지막 페이지 여부
     */
    private boolean last;

    /**
     * List<ReviewResponse>와 Page 정보를 받아서 ReviewListResponse로 변환
     */
    public static ReviewListResponse of(List<ReviewResponse> reviews, Page<?> page) {
        return ReviewListResponse.builder()
                .content(reviews)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .last(page.isLast())
                .build();
    }
}

