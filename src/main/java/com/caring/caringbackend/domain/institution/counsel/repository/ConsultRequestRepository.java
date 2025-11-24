package com.caring.caringbackend.domain.institution.counsel.repository;

import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequest;
import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 회원의 특정 상담 서비스에 대한 ACTIVE 상태 상담 요청 존재 여부 확인
     * - 같은 회원 + 같은 상담 서비스에 대해 ACTIVE 상태는 하나만 존재해야 함
     */
    @Query("""
            SELECT COUNT(cr) > 0
            FROM ConsultRequest cr
            WHERE cr.member.id = :memberId
            AND cr.counsel.id = :counselId
            AND cr.status = 'ACTIVE'
            AND cr.deleted = false
            """)
    boolean existsActiveByMemberAndCounsel(
            @Param("memberId") Long memberId,
            @Param("counselId") Long counselId
    );
    
    /**
     * 회원과 상담 서비스로 ACTIVE 상태 상담 요청 조회
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.member m
            JOIN FETCH cr.institution i
            JOIN FETCH cr.counsel c
            WHERE cr.member.id = :memberId
            AND cr.counsel.id = :counselId
            AND cr.status = 'ACTIVE'
            AND cr.deleted = false
            """)
    Optional<ConsultRequest> findActiveByMemberAndCounsel(
            @Param("memberId") Long memberId,
            @Param("counselId") Long counselId
    );

    /**
     * 회원의 상담 요청 목록 조회 (페이징)
     * - 마이페이지 상담 내역 조회용
     * - ChatRoom은 Service에서 별도 조회
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.institution i
            JOIN FETCH cr.counsel c
            WHERE cr.member.id = :memberId
            AND cr.deleted = false
            AND (:status IS NULL OR cr.status = :status)
            ORDER BY cr.createdAt DESC
            """)
    Page<ConsultRequest> findByMemberIdWithDetailsPaged(
            @Param("memberId") Long memberId,
            @Param("status") ConsultRequestStatus status,
            Pageable pageable
    );

    /**
     * 기관의 상담 요청 목록 조회 (페이징)
     * - 기관 관리자 상담 요청 관리용
     * - ChatRoom은 Service에서 별도 조회
     */
    @Query("""
            SELECT cr
            FROM ConsultRequest cr
            JOIN FETCH cr.member m
            JOIN FETCH cr.counsel c
            WHERE cr.institution.id = :institutionId
            AND cr.deleted = false
            AND (:status IS NULL OR cr.status = :status)
            ORDER BY cr.createdAt DESC
            """)
    Page<ConsultRequest> findByInstitutionIdWithDetailsPaged(
            @Param("institutionId") Long institutionId,
            @Param("status") ConsultRequestStatus status,
            Pageable pageable
    );
}

