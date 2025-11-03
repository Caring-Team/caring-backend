package com.caring.caringbackend.global.security.details;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class TemporaryInstitutionAdminDetails extends JwtUserDetails {
    private final String credentialType;

    @Builder
    public TemporaryInstitutionAdminDetails(String accessToken,
                                            Collection<? extends GrantedAuthority> authorities, String credentialType) {
        super(accessToken, authorities);
        this.credentialType = credentialType;
    }
}
