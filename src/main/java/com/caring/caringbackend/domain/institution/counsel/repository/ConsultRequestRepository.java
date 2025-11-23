package com.caring.caringbackend.domain.institution.counsel.repository;

import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequest;
import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultRequestRepository extends JpaRepository<ConsultRequest, Long> {
    
    /**
     * 회원의 상담 요청 목록 조회
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.institution i
            JOIN FETCH cr.counsel c
            WHERE cr.member.id = :memberId
            AND cr.deleted = false
            ORDER BY cr.createdAt DESC
            """)
    List<ConsultRequest> findByMemberIdWithDetails(@Param("memberId") Long memberId);
    
    /**
     * 기관의 상담 요청 목록 조회
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.member m
            JOIN FETCH cr.counsel c
            WHERE cr.institution.id = :institutionId
            AND cr.deleted = false
            ORDER BY cr.createdAt DESC
            """)
    List<ConsultRequest> findByInstitutionIdWithDetails(@Param("institutionId") Long institutionId);
    
    /**
     * 상담 요청 상세 조회 (연관 엔티티 포함)
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.member m
            JOIN FETCH cr.institution i
            JOIN FETCH cr.counsel c
            WHERE cr.id = :requestId
            AND cr.deleted = false
            """)
    Optional<ConsultRequest> findByIdWithDetails(@Param("requestId") Long requestId);
    
    /**
     * 회원의 특정 상담 서비스에 대한 활성 상담 요청 존재 여부 확인
     */
    @Query("""
            SELECT COUNT(cr) > 0
            FROM ConsultRequest cr
            WHERE cr.member.id = :memberId
            AND cr.counsel.id = :counselId
            AND cr.status IN :activeStatuses
            AND cr.deleted = false
            """)
    boolean existsActiveByCounselAndMember(
            @Param("memberId") Long memberId,
            @Param("counselId") Long counselId,
            @Param("activeStatuses") List<ConsultRequestStatus> activeStatuses
    );
}

