package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.ApprovalStatus;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionType;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Institution Repository
 * Spring Data JPA + QueryDSL + Custom Query
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long>, InstitutionRepositoryCustom {

    // ==================== Spring Data JPA 메서드 쿼리 ====================

    /**
     * 기관 이름으로 검색 (LIKE 검색, 페이징)
     */
    Page<Institution> findByNameContaining(String name, Pageable pageable);

    /**
     * 기관 유형과 입소 가능 여부로 검색
     */
    Page<Institution> findByInstitutionTypeAndIsAdmissionAvailable(
            InstitutionType institutionType,
            Boolean isAdmissionAvailable,
            Pageable pageable
    );

    @Query("""
                SELECT i FROM Institution i
                JOIN i.admins a
                WHERE a.id = :adminId
            """)
    Optional<Institution> findByAdminId(@Param("adminId") Long adminId);

    // ==================== @Query with JPQL ====================

    /**
     * JPQL을 사용한 복합 검색
     */
    @Query("""
            SELECT i FROM Institution i
            WHERE (:name IS NULL OR i.name LIKE %:name%)
            AND (:institutionType IS NULL OR i.institutionType = :institutionType)
            AND (:approvalStatus IS NULL OR i.approvalStatus = :approvalStatus)
            AND (:isAdmissionAvailable IS NULL OR i.isAdmissionAvailable = :isAdmissionAvailable)
            AND i.deleted = false
            ORDER BY i.createdAt DESC
            """)
    Page<Institution> searchInstitutions(
            @Param("name") String name,
            @Param("institutionType") InstitutionType institutionType,
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("isAdmissionAvailable") Boolean isAdmissionAvailable,
            Pageable pageable
    );

    /**
     * JPQL - 가격 범위 검색
     */
    @Query("""
            SELECT i FROM Institution i
            WHERE i.priceInfo.monthlyBaseFee <= :maxFee
            AND i.deleted = false
            """)
    Page<Institution> findByMaxMonthlyFee(@Param("maxFee") Integer maxFee, Pageable pageable);

    // ==================== Native Query ====================

    /**
     * Native Query - 거리 기반 검색
     * PostgreSQL의 Haversine Formula 사용
     * <p>
     * Note: 거리 계산 결과로 정렬하기 위해 ORDER BY를 쿼리에 포함
     * Pageable의 Sort는 무시되고 거리순으로 정렬됨
     */
    @Query(value = """
            SELECT i.*, 
                   (6371 * acos(
                       cos(radians(:latitude)) 
                       * cos(radians(i.latitude)) 
                       * cos(radians(i.longitude) - radians(:longitude)) 
                       + sin(radians(:latitude)) 
                       * sin(radians(i.latitude))
                   )) as distance
            FROM institution i
            WHERE i.deleted = false
            AND (6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(i.latitude)) 
                * cos(radians(i.longitude) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(i.latitude))
            )) <= :radiusKm
            ORDER BY distance
            """,
            countQuery = """
                    SELECT COUNT(*) FROM institution i
                    WHERE i.deleted = false
                    AND (6371 * acos(
                        cos(radians(:latitude)) 
                        * cos(radians(i.latitude)) 
                        * cos(radians(i.longitude) - radians(:longitude)) 
                        + sin(radians(:latitude)) 
                        * sin(radians(i.latitude))
                    )) <= :radiusKm
                    """,
            nativeQuery = true)
    Page<Institution> findByDistanceNative(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );
}
