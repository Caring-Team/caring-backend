package com.caring.caringbackend.domain.user.guardian.repository;

import com.caring.caringbackend.domain.user.guardian.entity.AuthCredential;
import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthCredentialRepository extends JpaRepository<AuthCredential, Long> {

    public Optional<AuthCredential> findAuthCredentialByIdentifierAndType(String identifier, CredentialType type);

    public Optional<AuthCredential> findAuthCredentialByMemberAndType(Member member, CredentialType type);

    public boolean existsByIdentifierAndType(final String identifier, CredentialType type);
}
