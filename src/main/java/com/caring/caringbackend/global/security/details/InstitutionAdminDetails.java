package com.caring.caringbackend.global.security.details;

import java.util.Collection;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

public class InstitutionAdminDetails extends JwtUserDetails {

    private final Long id;

    @Builder
    public InstitutionAdminDetails(String accessToken, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(accessToken, authorities);
        this.id = id;
    }
}
