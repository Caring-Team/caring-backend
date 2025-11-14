package com.caring.caringbackend.domain.institution.counsel.repository;

import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface InstitutionCounselDetailRepository extends JpaRepository<InstitutionCounselDetail, Long> {

    /**
     * 상담 서비스 ID와 날짜로 Detail 조회
     */
    @Query("""
            SELECT d 
            FROM InstitutionCounselDetail d 
            WHERE d.institutionCounsel.id = :counselId 
            AND d.serviceDate = :serviceDate
            """)
    Optional<InstitutionCounselDetail> findByCounselIdAndServiceDate(
            @Param("counselId") Long counselId,
            @Param("serviceDate") LocalDate serviceDate
    );
}
