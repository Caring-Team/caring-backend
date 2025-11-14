package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.CareGiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {

    /**
     * 기관 ID로 요양보호사 목록 조회 (전체, 생성일 내림차순)
     * 삭제되지 않은 요양보호사만 조회
     */
    @Query("""
            SELECT c 
            FROM CareGiver c 
            WHERE c.institution.id = :institutionId 
            AND c.deleted = false 
            ORDER BY c.createdAt DESC
            """)
    List<CareGiver> findByInstitutionIdOrderByCreatedAtDesc(@Param("institutionId") Long institutionId);

    /**
     * 기관 ID와 요양보호사 ID로 조회
     * 권한 검증용
     */
    @Query("""
            SELECT c 
            FROM CareGiver c 
            WHERE c.id = :careGiverId 
            AND c.institution.id = :institutionId 
            AND c.deleted = false
            """)
    Optional<CareGiver> findByIdAndInstitutionId(
            @Param("careGiverId") Long careGiverId,
            @Param("institutionId") Long institutionId
    );
}
