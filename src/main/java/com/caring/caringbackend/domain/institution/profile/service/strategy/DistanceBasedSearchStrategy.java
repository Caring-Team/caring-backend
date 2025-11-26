package com.caring.caringbackend.domain.institution.profile.service.strategy;

import com.caring.caringbackend.api.internal.institution.dto.request.InstitutionSearchFilter;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionRepository;
import com.caring.caringbackend.domain.institution.profile.service.InstitutionSearchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 거리 기반 검색 전략 (Native Query 사용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistanceBasedSearchStrategy implements InstitutionSearchStrategy {

    private final InstitutionRepository institutionRepository;

    @Override
    public boolean isApplicable(InstitutionSearchFilter filter) {
        return filter != null
                && filter.getLatitude() != null
                && filter.getLongitude() != null
                && filter.getRadiusKm() != null;
    }

    @Override
    public Page<Institution> search(InstitutionSearchFilter filter, Pageable pageable) {
        log.info("거리 기반 검색 수행: lat={}, lng={}, radius={}km",
                filter.getLatitude(), filter.getLongitude(), filter.getRadiusKm());

        // Native Query에 ORDER BY가 포함되어 있으므로 Sort를 제거한 Pageable 생성
        Pageable pageableWithoutSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return institutionRepository.findByDistanceNative(
                filter.getLatitude(),
                filter.getLongitude(),
                filter.getRadiusKm(),
                pageableWithoutSort
        );
    }

    @Override
    public int getPriority() {
        return 1; // 가장 높은 우선순위
    }
}
