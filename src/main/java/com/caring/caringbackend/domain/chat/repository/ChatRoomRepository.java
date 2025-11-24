package com.caring.caringbackend.domain.chat.repository;

import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * ConsultRequest ID로 채팅방 조회
     */
    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.consultRequest req
            WHERE req.id = :consultRequestId
            """)
    Optional<ChatRoom> findByConsultRequestId(@Param("consultRequestId") Long consultRequestId);

    /**
     * 채팅방 ID와 회원 ID로 조회 (권한 검증용)
     * - 회원이 자신의 채팅방에만 접근할 수 있도록 검증
     */
    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.consultRequest req
            JOIN FETCH req.member m
            WHERE cr.id = :chatRoomId
            AND m.id = :memberId
            """)
    Optional<ChatRoom> findByIdAndMemberId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("memberId") Long memberId
    );

    /**
     * 채팅방 ID와 기관 ID로 조회 (권한 검증용)
     * - 기관 관리자가 자신의 기관 채팅방에만 접근할 수 있도록 검증
     */
    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.consultRequest req
            JOIN FETCH req.institution i
            WHERE cr.id = :chatRoomId
            AND i.id = :institutionId
            """)
    Optional<ChatRoom> findByIdAndInstitutionId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("institutionId") Long institutionId
    );

    /**
     * 채팅방 존재 여부 확인 (ConsultRequest 기준)
     * - 중복 생성 방지
     */
    boolean existsByConsultRequestId(Long consultRequestId);

    /**
     * ConsultRequest ID 리스트로 채팅방 배치 조회
     * - N+1 문제 방지를 위한 배치 조회
     * - 상담 내역 목록 조회 시 사용
     */
    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.consultRequest req
            WHERE req.id IN :consultRequestIds
            """)
    List<ChatRoom> findAllByConsultRequestIdIn(@Param("consultRequestIds") List<Long> consultRequestIds);

    /**
     * 채팅방 ID로 조회 (ConsultRequest, Member, Institution 포함, JOIN FETCH)
     * - sendMessage 등에서 사용
     * - Lazy Loading 방지 (getMemberId(), getInstitutionId() 호출 시 필요)
     */
    @Query("""
            SELECT cr FROM ChatRoom cr
            JOIN FETCH cr.consultRequest req
            JOIN FETCH req.member m
            JOIN FETCH req.institution i
            WHERE cr.id = :chatRoomId
            """)
    Optional<ChatRoom> findByIdWithConsultRequest(@Param("chatRoomId") Long chatRoomId);
}

