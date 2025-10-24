package com.caring.caringbackend.global.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 🏗️ 공통 베이스 엔티티
 *
 * 모든 엔티티의 기본 필드를 제공합니다.
 * - 생성일시/수정일시: JPA Auditing을 통한 자동 관리
 * - 소프트 삭제: 물리적 삭제 대신 논리적 삭제 지원
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 📅 생성일시
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 🔄 수정일시
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 🗑️ 소프트 삭제 여부
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * 🗑️ 삭제일시
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 🗑️ 소프트 삭제 처리
     *
     * 엔티티를 물리적으로 삭제하지 않고 논리적으로 삭제 처리합니다.
     */
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 🔄 삭제 복구
     *
     * 소프트 삭제된 엔티티를 복구합니다.
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }

    /**
     * ✅ 활성 상태 확인
     *
     * @return 삭제되지 않은 상태인지 여부
     */
    public boolean isActive() {
        return !this.deleted;
    }

    /**
     * 🗑️ 삭제 상태 확인
     *
     * @return 삭제된 상태인지 여부
     */
    public boolean isDeleted() {
        return this.deleted;
    }
}
