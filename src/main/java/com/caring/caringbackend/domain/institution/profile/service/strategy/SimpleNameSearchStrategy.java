package com.caring.caringbackend.domain.institution.profile.service.strategy;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 단순 이름 검색 전략 (Spring Data JPA)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleNameSearchStrategy implements InstitutionSearchStrategy {

    private final InstitutionRepository institutionRepository;

    @Override
    public boolean isApplicable(InstitutionSearchFilter filter) {
        return filter != null && filter.getName() != null;
    }

    @Override
    public Page<Institution> search(InstitutionSearchFilter filter, Pageable pageable) {
        log.info("이름 검색 수행: name={}", filter.getName());
        return institutionRepository.findByNameContaining(filter.getName(), pageable);
    }

    @Override
    public int getPriority() {
        return 3;
    }
}

