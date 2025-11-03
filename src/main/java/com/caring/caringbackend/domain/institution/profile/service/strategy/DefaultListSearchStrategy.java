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
 * 기본 목록 조회 전략 (필터 없음)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultListSearchStrategy implements InstitutionSearchStrategy {

    private final InstitutionRepository institutionRepository;

    @Override
    public boolean isApplicable(InstitutionSearchFilter filter) {
        return true; // 항상 적용 가능 (fallback)
    }

    @Override
    public Page<Institution> search(InstitutionSearchFilter filter, Pageable pageable) {
        return institutionRepository.findAll(pageable);
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE; // 가장 낮은 우선순위 (fallback)
    }
}

