package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstitutionReviewServiceImpl implements InstitutionReviewService {

    private final ReviewRepository reviewRepository;
    private final int RECENT_REVIEW_DAYS = 7;

    @Override
    @Transactional(readOnly = true)
    public Long getRecentReviewCount(Long institutionId) {
        LocalDateTime from = LocalDateTime.now().minusDays(RECENT_REVIEW_DAYS);
        return reviewRepository.countRecentReviews(institutionId, from)
                .getRecentReviewCount();
    }
}
