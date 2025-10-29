package com.caring.caringbackend.domain.institution.profile.service.strategy;

import com.caring.caringbackend.api.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * QueryDSL 기반 복합 검색 전략
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueryDslSearchStrategy implements InstitutionSearchStrategy {

    private final InstitutionRepository institutionRepository;

    @Override
    public boolean isApplicable(InstitutionSearchFilter filter) {
        if (filter == null) {
            return false;
        }

        return filter.getName() != null
                || filter.getInstitutionType() != null
                || filter.getApprovalStatus() != null
                || filter.getCity() != null
                || filter.getMinBedCount() != null
                || filter.getMaxBedCount() != null
                || filter.getMaxMonthlyFee() != null;
    }

    @Override
    public Page<Institution> search(InstitutionSearchFilter filter, Pageable pageable) {
        log.info("QueryDSL 동적 검색 수행: filter={}", filter);
        return institutionRepository.searchWithQueryDsl(filter, pageable);
    }

    @Override
    public int getPriority() {
        return 2; // 거리 기반 다음 우선순위
    }
}

