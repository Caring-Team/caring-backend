package com.caring.caringbackend.domain.chat.entity;

import com.caring.caringbackend.domain.institution.counsel.entity.ConsultRequest;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 엔티티
 * - ConsultRequest와 1:1 관계
 * - 하나의 상담 요청당 하나의 채팅방
 * - 회원 1명 ↔ 기관 관리자 여러 명 (다대일)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상담 요청 (1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_request_id", nullable = false, unique = true)
    private ConsultRequest consultRequest;

    // 마지막 메시지 내용 (미리보기용)
    @Column(length = 500)
    private String lastMessageContent;

    // 마지막 메시지 전송 시간
    private LocalDateTime lastMessageAt;

    // 채팅방 활성화 여부 (상담 종료 시 false)
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder
    private ChatRoom(ConsultRequest consultRequest) {
        this.consultRequest = consultRequest;
        this.isActive = true;
    }

    /**
     * 채팅방 생성 정적 팩토리 메서드
     * - 상담 시작 시 ConsultRequest와 함께 생성됨
     */
    public static ChatRoom create(ConsultRequest consultRequest) {
        return ChatRoom.builder()
                .consultRequest(consultRequest)
                .build();
    }

    /**
     * 마지막 메시지 업데이트
     * - 새 메시지 전송 시 호출
     */
    public void updateLastMessage(String content, LocalDateTime sentAt) {
        this.lastMessageContent = content;
        this.lastMessageAt = sentAt;
    }

    /**
     * 채팅방 비활성화 (상담 종료 시)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 채팅방 재활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 회원 ID 조회 (편의 메서드)
     */
    public Long getMemberId() {
        return consultRequest.getMember().getId();
    }

    /**
     * 기관 ID 조회 (편의 메서드)
     */
    public Long getInstitutionId() {
        return consultRequest.getInstitution().getId();
    }

    /**
     * 기관명 조회 (편의 메서드)
     * - 기관 관리자 메시지는 기관명으로 표시
     */
    public String getInstitutionName() {
        return consultRequest.getInstitution().getName();
    }
}

