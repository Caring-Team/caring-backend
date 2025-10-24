package com.caring.caringbackend.domain.user.guardian.entity;

import com.caring.caringbackend.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 정보 엔티티
 * <p>
 * 회원의 로그인 인증 정보를 관리합니다.
 * 로컬 로그인(이메일/비밀번호) 또는 소셜 로그인(카카오, 네이버, 구글) 정보를 저장합니다.
 * 한 회원은 여러 인증 수단을 가질 수 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor

public class AuthCredential extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public Member member;

    // credential type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType type;

    // credential identifier 로컬이면 email, 소셜이면 provider_id
    @Column(nullable = false)
    private String identifier;

    // password hash 값
    @Column(length = 255)
    private String passwordHash;

    @Builder
    private AuthCredential(Member member, CredentialType type, String identifier,
                           String passwordHash, Boolean isVerifiedByPhone) {
        this.member = member;
        this.type = type;
        this.identifier = identifier;
        this.passwordHash = passwordHash;
    }

    // 정적 팩토리 메소드
    // 로컬 패스워드 인증 정보 생성
    public static AuthCredential createLocalCredential(Member member, String email, String passwordHash) {
        return AuthCredential.builder()
                .member(member)
                .type(CredentialType.LOCAL)
                .identifier(email)
                .passwordHash(passwordHash)
                .isVerifiedByPhone(false)
                .build();
    }

    // 소셜 인증 정보 생성
    public static AuthCredential createSocialCredential(Member member, CredentialType type, String providerId) {
        return AuthCredential.builder()
                .member(member)
                .type(type)
                .identifier(providerId)
                .passwordHash(null)
                .isVerifiedByPhone(false)
                .build();
    }

    // TODO: 필요한 도메인 로직 작성
}
