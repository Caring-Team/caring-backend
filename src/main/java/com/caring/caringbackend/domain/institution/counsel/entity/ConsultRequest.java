package com.caring.caringbackend.domain.institution.counsel.entity;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상담 요청 엔티티
 * <p>
 * 회원이 기관에 상담을 요청한 정보를 관리합니다.
 * 상담 요청 1건당 채팅방 1개가 생성됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consult_request")
public class ConsultRequest extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 상담 요청 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    // 상담 받을 기관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    // 기관이 제공하는 상담 서비스 종류
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_id", nullable = false)
    private InstitutionCounsel counsel;
    
    // 회원이 상담 요청 시 입력한 내용
    @Column(length = 1000)
    private String message;
    
    // 상담 요청 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConsultRequestStatus status = ConsultRequestStatus.PENDING;
    
    @Builder
    private ConsultRequest(Member member, Institution institution, InstitutionCounsel counsel, String message) {
        this.member = member;
        this.institution = institution;
        this.counsel = counsel;
        this.message = message;
        this.status = ConsultRequestStatus.PENDING;
    }
    
    /**
     * 상담 요청 생성
     */
    public static ConsultRequest create(Member member, Institution institution, InstitutionCounsel counsel, String message) {
        return ConsultRequest.builder()
                .member(member)
                .institution(institution)
                .counsel(counsel)
                .message(message)
                .build();
    }
    
    /**
     * 상담 요청 상태 변경
     */
    public void updateStatus(ConsultRequestStatus newStatus) {
        this.status = newStatus;
    }
    
    /**
     * 상담 요청 수락
     */
    public void accept() {
        this.status = ConsultRequestStatus.ACCEPTED;
    }
    
    /**
     * 상담 요청 거절
     */
    public void reject() {
        this.status = ConsultRequestStatus.REJECTED;
    }
    
    /**
     * 상담 시작
     */
    public void startConsult() {
        this.status = ConsultRequestStatus.IN_PROGRESS;
    }
    
    /**
     * 상담 완료
     */
    public void complete() {
        this.status = ConsultRequestStatus.COMPLETED;
    }
    
    /**
     * 상담 요청 취소
     */
    public void cancel() {
        this.status = ConsultRequestStatus.CANCELLED;
    }
    
    /**
     * 상담 요청이 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == ConsultRequestStatus.PENDING 
            || status == ConsultRequestStatus.ACCEPTED 
            || status == ConsultRequestStatus.IN_PROGRESS;
    }
}

