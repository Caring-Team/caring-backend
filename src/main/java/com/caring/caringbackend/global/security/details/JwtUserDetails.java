package com.caring.caringbackend.global.security.details;

import java.io.Serializable;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public abstract class JwtUserDetails implements Serializable {

    final String accessToken;
    final Collection<? extends GrantedAuthority> authorities;

}
