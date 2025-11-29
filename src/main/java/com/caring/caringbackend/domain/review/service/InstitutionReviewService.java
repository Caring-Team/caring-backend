package com.caring.caringbackend.domain.review.service;

import com.caring.caringbackend.api.internal.Member.dto.review.response.ReviewListResponse;
import com.caring.caringbackend.api.internal.institution.dto.request.review.InstitutionReviewSearchFilter;
import com.caring.caringbackend.api.internal.institution.dto.response.review.InstitutionReviewsResponseDto;
import org.springframework.data.domain.Pageable;

public interface InstitutionReviewService {

    ReviewListResponse getInstitutionReviews(Long institutionId, Pageable pageable);

    InstitutionReviewsResponseDto getInstitutionDetailReviews(Long institutionId);

    InstitutionReviewsResponseDto getMyInstitutionReviews(Long adminId, InstitutionReviewSearchFilter searchFilter);

    Long getRecentReviewCount(Long institutionId);
}
