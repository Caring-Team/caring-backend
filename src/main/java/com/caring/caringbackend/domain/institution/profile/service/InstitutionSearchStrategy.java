package com.caring.caringbackend.domain.institution.profile.service;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 기관 검색 전략 인터페이스
 */
public interface InstitutionSearchStrategy {

    /**
     * 검색 전략 적용 가능 여부 확인
     */
    boolean isApplicable(InstitutionSearchFilter filter);

    /**
     * 검색 실행
     */
    Page<Institution> search(InstitutionSearchFilter filter, Pageable pageable);

    /**
     * 전략 우선순위 (낮을수록 우선)
     */
    int getPriority();
}

