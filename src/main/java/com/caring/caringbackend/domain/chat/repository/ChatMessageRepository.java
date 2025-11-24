package com.caring.caringbackend.domain.chat.repository;

import com.caring.caringbackend.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 채팅방의 메시지 목록 조회 (페이징, Soft Delete 제외)
     * - 삭제되지 않은 메시지만 조회
     * - 최신순 정렬
     */
    @Query("""
            SELECT cm FROM ChatMessage cm
            WHERE cm.chatRoom.id = :chatRoomId
            AND cm.deleted = false
            ORDER BY cm.createdAt DESC
            """)
    Page<ChatMessage> findByChatRoomIdAndNotDeleted(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );

    /**
     * 롱 폴링용: 특정 메시지 ID 이후의 신규 메시지 조회
     * - lastMessageId보다 큰 ID를 가진 메시지만 조회
     * - 삭제되지 않은 메시지만 조회
     * - 오래된 순 정렬 (클라이언트에서 순서대로 표시하기 위해)
     */
    @Query("""
            SELECT cm FROM ChatMessage cm
            WHERE cm.chatRoom.id = :chatRoomId
            AND cm.id > :lastMessageId
            AND cm.deleted = false
            ORDER BY cm.createdAt ASC
            """)
    List<ChatMessage> findNewMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("lastMessageId") Long lastMessageId
    );

    /**
     * 채팅방의 전체 메시지 수 조회 (Soft Delete 제외)
     */
    @Query("""
            SELECT COUNT(cm)
            FROM ChatMessage cm
            WHERE cm.chatRoom.id = :chatRoomId
            AND cm.deleted = false
            """)
    long countByChatRoomIdAndNotDeleted(@Param("chatRoomId") Long chatRoomId);

    /**
     * 채팅방의 마지막 메시지 조회 (Soft Delete 제외)
     * - 채팅방 목록에서 미리보기용
     * - Spring Data JPA 메서드 네이밍으로 LIMIT 1 자동 적용
     */
    ChatMessage findFirstByChatRoomIdAndDeletedFalseOrderByCreatedAtDesc(Long chatRoomId);
}

