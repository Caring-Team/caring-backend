package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.api.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Institution Repository Custom Interface
 *
 * QueryDSL을 사용한 동적 쿼리를 위한 인터페이스
 */
public interface InstitutionRepositoryCustom {

    /**
     * QueryDSL을 사용한 동적 검색
     *
     * @param filter 검색 필터
     * @param pageable 페이징 정보
     * @return 검색 결과 페이지
     */
    Page<Institution> searchWithQueryDsl(InstitutionSearchFilter filter, Pageable pageable);
}

