package com.caring.caringbackend.global.jwt.details;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;


@Getter
public class TemporaryUserDetails extends JwtUserDetails {

    private final String credentialType;
    private final String credentialId;

    @Builder
    public TemporaryUserDetails(
            String accessToken,
            Collection<? extends GrantedAuthority> authorities,
            String credentialType, String credentialId) {
        super(accessToken, authorities);
        this.credentialType = credentialType;
        this.credentialId = credentialId;
    }
}
