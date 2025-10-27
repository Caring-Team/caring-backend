package com.caring.caringbackend.global.jwt.details;


import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class MemberDetails extends JwtUserDetails {
    private final Long id;

    @Builder
    public MemberDetails(
            String accessToken,
            Collection<? extends GrantedAuthority> authorities,
            Long id) {
        super(accessToken, authorities);
        this.id = id;
    }
}
