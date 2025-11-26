package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstitutionAdvertisementCustom {

    /**
     * 관리자용 광고 목록 필터링 (페이징 있음)
     * */
    Page<InstitutionAdvertisement> findAllWithFilters(
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    );
}
