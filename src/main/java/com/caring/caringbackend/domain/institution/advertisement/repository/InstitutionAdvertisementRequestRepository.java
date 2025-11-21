package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 광고 신청 Repository
 */
@Repository
public interface InstitutionAdvertisementRequestRepository extends JpaRepository<InstitutionAdvertisementRequest, Long>, InstitutionAdvertisementRequestCustom {

    // ==================== 기관용 ====================

    /**
     * 신청 ID와 기관 ID로 조회 (권한 확인용)
     */
    Optional<InstitutionAdvertisementRequest> findByIdAndInstitutionId(Long id, Long institutionId);

    // ==================== 관리자용 (페이징) ====================

    /**
     * 상태별 신청 목록 조회 (관리자용, 페이징)
     */
    Page<InstitutionAdvertisementRequest> findByStatus(AdvertisementStatus status, Pageable pageable);
}

