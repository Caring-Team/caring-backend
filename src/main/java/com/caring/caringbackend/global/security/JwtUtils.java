package com.caring.caringbackend.global.security;

import com.caring.caringbackend.domain.auth.dto.GenerateTemporaryTokenDto;
import com.caring.caringbackend.domain.auth.dto.GenerateTokenDto;
import com.caring.caringbackend.domain.auth.dto.RefreshTokenPayloadDto;
import com.caring.caringbackend.domain.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdminRole;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.JwtUserDetails;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.TemporaryUserDetails;
import com.caring.caringbackend.global.security.details.MemberDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;
    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.access-token-validity}") long accessTokenValidityInSeconds,
                    @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInSeconds
    ) {
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtUserDetails decodeJwtUserDetails(String token) {
        Claims payload = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
        String role = payload.get("role", String.class);
        if (MemberRole.USER.getKey().equals(role)) {
            return MemberDetails.builder()
                    .id(payload.get("id", Long.class))
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                    .accessToken(token)
                    .build();
        } else if (MemberRole.TEMP_LOCAL.getKey().equals(role)) {
            String credentialType = payload.get("credential_type", String.class);
            return TemporaryUserDetails.builder()
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                    .credentialType(credentialType)
                    .accessToken(token)
                    .build();
        } else if (MemberRole.TEMP_OAUTH.getKey().equals(role)) {
            String credentialType = payload.get("credential_type", String.class);
            String credentialId = payload.get("credential_id", String.class);
            return TemporaryUserDetails.builder()
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                    .credentialType(credentialType)
                    .credentialId(credentialId)
                    .accessToken(token)
                    .build();
        } else if (InstitutionAdminRole.TEMP_INSTITUTION.getKey().equals(role)) {
            return TemporaryInstitutionAdminDetails.builder()
                    .accessToken(token)
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                    .build();
        } else if (InstitutionAdminRole.STAFF.getKey().equals(role) ||
                InstitutionAdminRole.OWNER.getKey().equals(role)) {
            return InstitutionAdminDetails.builder()
                    .id(payload.get("id", Long.class))
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                    .accessToken(token)
                    .build();
        }
        return null;
    }

    public RefreshTokenPayloadDto decodeRefreshToken(String token) {
        Claims payload = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
        Long id = payload.get("id", Long.class);

        return RefreshTokenPayloadDto.builder()
                .id(id)
                .build();
    }

    public JwtTokenResponse generateToken(GenerateTokenDto dto) {
        String accessToken = makeAccessToken(dto);
        String refreshToken = makeRefreshToken(dto);
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(accessTokenValidityInSeconds)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshTokenValidityInSeconds)
                .build();
    }

    public JwtTokenResponse generateTemporaryToken(GenerateTemporaryTokenDto dto) {
        String token = Jwts.builder()
                .claim("role", dto.getRole())
                .claim("credential_type", dto.getCredentialType())
                .claim("credential_id", dto.getCredentialId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .signWith(secretKey)
                .compact();
        return JwtTokenResponse.builder()
                .accessToken(token)
                .expiresIn(accessTokenValidityInSeconds)
                .build();
    }


    public JwtTokenResponse regenerateAccessToken(GenerateTokenDto dto) {
        String accessToken = makeAccessToken(dto);
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(accessTokenValidityInSeconds)
                .build();
    }

    private String makeAccessToken(GenerateTokenDto dto) {
        return Jwts.builder()
                .claim("id", dto.getId())
                .claim("role", dto.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .signWith(secretKey)
                .compact();
    }

    private String makeRefreshToken(GenerateTokenDto dto) {
        return Jwts.builder()
                .claim("id", dto.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .signWith(secretKey)
                .compact();
    }

    public Boolean isTokenExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Long getTokenRemainTime(String token) {
        return (Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().getExpiration().getTime() - System.currentTimeMillis()) / 1000;
    }
}
