package com.caring.caringbackend.domain.institution.counsel.repository;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounsel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstitutionCounselRepository extends JpaRepository<InstitutionCounsel, Long> {

    @Query("""
            SELECT ic
            FROM InstitutionCounsel ic
            WHERE ic.institution.id = :institutionId
            ORDER BY ic.createdAt DESC
           """)
    List<InstitutionCounsel> findByInstitutionId(@Param("institutionId") Long institutionId);
}
