package com.caring.caringbackend.domain.institution.advertisement.repository;

import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementStatus;
import com.caring.caringbackend.domain.institution.advertisement.entity.AdvertisementType;
import com.caring.caringbackend.domain.institution.advertisement.entity.InstitutionAdvertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 기관 광고 Repository
 */
@Repository
public interface InstitutionAdvertisementRepository extends JpaRepository<InstitutionAdvertisement, Long> {

    // ==================== 기관용 (페이징 없음) ====================

    /**
     * 기관 ID로 광고 목록 조회
     */
    List<InstitutionAdvertisement> findByInstitutionId(Long institutionId);

    /**
     * 기관 ID와 상태로 광고 목록 조회
     */
    List<InstitutionAdvertisement> findByInstitutionIdAndStatus(Long institutionId, AdvertisementStatus status);

    // ==================== 관리자용 (페이징) ====================

    /**
     * 기관 ID로 광고 목록 조회 (페이징) - 관리자용
     */
    Page<InstitutionAdvertisement> findByInstitutionId(Long institutionId, Pageable pageable);


    /**
     * 기관 ID, 상태, 광고 유형으로 광고 목록 조회 (페이징)
     */
    Page<InstitutionAdvertisement> findByInstitutionIdAndStatusAndType(
            Long institutionId,
            AdvertisementStatus status,
            AdvertisementType type,
            Pageable pageable
    );

    /**
     * 광고 ID와 기관 ID로 조회 (권한 확인용)
     */
    Optional<InstitutionAdvertisement> findByIdAndInstitutionId(Long id, Long institutionId);

    /**
     * 승인 대기 광고 목록 조회 (관리자용, 페이징)
     */
    Page<InstitutionAdvertisement> findByStatus(AdvertisementStatus status, Pageable pageable);

    /**
     * 특정 상태와 시작 시간 이전인 광고 목록 조회 (스케줄러용)
     */
    List<InstitutionAdvertisement> findByStatusAndStartDateTimeBefore(
            AdvertisementStatus status,
            LocalDateTime dateTime
    );

    /**
     * 특정 상태와 종료 시간 이전인 광고 목록 조회 (스케줄러용)
     */
    List<InstitutionAdvertisement> findByStatusAndEndDateTimeBefore(
            AdvertisementStatus status,
            LocalDateTime dateTime
    );

    /**
     * 현재 진행중인 광고 목록 조회
     */
    @Query("SELECT ad FROM InstitutionAdvertisement ad " +
            "WHERE ad.status = :status " +
            "AND ad.deleted = false " +
            "AND ad.startDateTime <= :now " +
            "AND ad.endDateTime >= :now " +
            "ORDER BY ad.createdAt DESC")
    List<InstitutionAdvertisement> findActiveAdvertisements(
            @Param("status") AdvertisementStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * 유형별 현재 진행중인 광고 목록 조회
     */
    @Query("SELECT ad FROM InstitutionAdvertisement ad " +
            "WHERE ad.status = :status " +
            "AND ad.type = :type " +
            "AND ad.deleted = false " +
            "AND ad.startDateTime <= :now " +
            "AND ad.endDateTime >= :now " +
            "ORDER BY ad.createdAt DESC")
    List<InstitutionAdvertisement> findActiveAdvertisementsByType(
            @Param("status") AdvertisementStatus status,
            @Param("type") AdvertisementType type,
            @Param("now") LocalDateTime now
    );

    /**
     * 특정 기간 동안 같은 기관의 같은 유형 광고가 있는지 확인 (중복 체크)
     */
    @Query("SELECT COUNT(ad) > 0 FROM InstitutionAdvertisement ad " +
            "WHERE ad.institution.id = :institutionId " +
            "AND ad.type = :type " +
            "AND ad.status IN ('REQUEST_PENDING', 'REQUEST_APPROVED', 'ADVERTISEMENT_PENDING', 'ADVERTISEMENT_ACTIVE') " +
            "AND ((ad.startDateTime <= :endDateTime AND ad.endDateTime >= :startDateTime))")
    boolean existsOverlappingAdvertisement(
            @Param("institutionId") Long institutionId,
            @Param("type") AdvertisementType type,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 특정 유형의 동시 진행중인 광고 개수 조회
     */
    @Query("SELECT COUNT(ad) FROM InstitutionAdvertisement ad " +
            "WHERE ad.type = :type " +
            "AND ad.status = 'ADVERTISEMENT_ACTIVE' " +
            "AND ad.deleted = false " +
            "AND ad.startDateTime <= :now " +
            "AND ad.endDateTime >= :now")
    long countActiveAdvertisementsByType(
            @Param("type") AdvertisementType type,
            @Param("now") LocalDateTime now
    );
}

