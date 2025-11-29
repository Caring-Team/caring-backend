package com.caring.caringbackend.domain.tag.repository;

import com.caring.caringbackend.domain.tag.entity.InstitutionTag;
import com.caring.caringbackend.domain.tag.entity.Tag;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 기관 태그 Repository
 * 
 * @author 윤다인
 * @since 2025-11-19
 */
@Repository
public interface InstitutionTagRepository extends JpaRepository<InstitutionTag, Long> {
    
    /**
     * 기관 ID로 기관 태그 목록 조회
     * 
     * @param institutionId 기관 ID
     * @return 기관 태그 목록
     */
    List<InstitutionTag> findByInstitutionId(Long institutionId);


    @Query("""
    SELECT it.tag
    FROM InstitutionTag it
    WHERE it.institution.id = :institutionId
    """)
    List<Tag> findTagsByInstitutionId(Long institutionId);


    @Query("""
    SELECT it.tag.id
    FROM InstitutionTag it
    WHERE it.institution.id = :institutionId
    """)
    Set<Long> findTagIdsByInstitutionId(Long institutionId);
    
    /**
     * 기관 ID로 기관 태그 전체 삭제
     * 
     * @param institutionId 기관 ID
     */
    void deleteByInstitutionId(Long institutionId);
}

