package com.caring.caringbackend.global.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 🗄️ JPA 설정
 *
 * JPA Auditing 기능을 활성화하여 엔티티의 생성/수정 시간을 자동 관리
 *
 * @author caring-team
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    // JPA Auditing 활성화
    // - @CreatedDate: 엔티티 생성 시 자동으로 현재 시간 설정
    // - @LastModifiedDate: 엔티티 수정 시 자동으로 현재 시간 업데이트
}
