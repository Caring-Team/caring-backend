package com.caring.caringbackend.api.auth.dto;

import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdminRole;
import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerateTemporaryTokenDto {

    private String credentialType;
    private String credentialId;
    private String role;


    public static GenerateTemporaryTokenDto of(CredentialType type, String credentialId, MemberRole role) {
        return GenerateTemporaryTokenDto.builder()
                .credentialType(type.getKey())
                .credentialId(credentialId)
                .role(role.getKey())
                .build();
    }

    public static GenerateTemporaryTokenDto of(CredentialType type, String credentialId, InstitutionAdminRole role) {
        return GenerateTemporaryTokenDto.builder()
                .credentialType(type.getKey())
                .credentialId(credentialId)
                .role(role.getKey())
                .build();
    }
}
