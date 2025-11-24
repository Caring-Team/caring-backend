package com.caring.caringbackend.domain.chat.entity;

import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 엔티티
 * - 채팅방의 개별 메시지
 * - Soft Delete 지원
 * - 발신자 유형(회원/기관 관리자) 구분
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_room_id_created_at", columnList = "chat_room_id, created_at"),
        @Index(name = "idx_chat_room_id_id", columnList = "chat_room_id, id")
})
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 발신자 유형 (회원 or 기관 관리자)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SenderType senderType;

    // 발신자 ID (Member.id or InstitutionAdmin.id)
    @Column(nullable = false)
    private Long senderId;

    // 메시지 내용
    @Column(nullable = false, length = 2000)
    private String content;

    // Soft Delete (삭제된 메시지 여부)
    @Column(nullable = false)
    private Boolean deleted = false;

    @Builder
    private ChatMessage(ChatRoom chatRoom, SenderType senderType, Long senderId, String content) {
        this.chatRoom = chatRoom;
        this.senderType = senderType;
        this.senderId = senderId;
        this.content = content;
        this.deleted = false;
    }

    /**
     * 메시지 생성 정적 팩토리 메서드
     */
    public static ChatMessage create(ChatRoom chatRoom, SenderType senderType, Long senderId, String content) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderType(senderType)
                .senderId(senderId)
                .content(content)
                .build();
    }

    /**
     * 메시지 삭제 (Soft Delete)
     * - 실제 데이터는 유지하되 deleted = true로 표시
     */
    public void delete() {
        this.deleted = true;
    }

    /**
     * 메시지가 삭제되었는지 확인
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * 회원이 보낸 메시지인지 확인
     */
    public boolean isMemberMessage() {
        return this.senderType == SenderType.MEMBER;
    }

    /**
     * 기관 관리자가 보낸 메시지인지 확인
     */
    public boolean isInstitutionAdminMessage() {
        return this.senderType == SenderType.INSTITUTION_ADMIN;
    }
}

