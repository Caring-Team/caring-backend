package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstitutionAdvertisementRequestCustom {

    /**
     * 기관 ID로 필터링 (페이징 없음)
     */
    List<InstitutionAdvertisementRequest> findByInstitutionIdWithFilters(
            Long institutionId,
            AdvertisementStatus status,
            AdvertisementType type
    );

    /**
     * 전체 목록 필터링 (페이징 있음) - 관리자용
     */
    Page<InstitutionAdvertisementRequest> findAllWithFilters(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    );
}
