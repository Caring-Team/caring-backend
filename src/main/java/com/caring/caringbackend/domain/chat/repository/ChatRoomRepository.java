package com.caring.caringbackend.domain.chat.repository;

import com.caring.caringbackend.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

