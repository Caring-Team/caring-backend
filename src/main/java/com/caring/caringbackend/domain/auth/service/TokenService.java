package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.dto.GenerateTemporaryTokenDto;
import com.caring.caringbackend.domain.auth.dto.GenerateTokenDto;
import com.caring.caringbackend.domain.auth.dto.RefreshTokenPayloadDto;
import com.caring.caringbackend.domain.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.domain.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.entity.RefreshToken;
import com.caring.caringbackend.domain.auth.repository.RefreshTokenRepository;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    @Transactional
    public JwtTokenResponse generateToken(GenerateTokenDto generateTokenDto) {

        JwtTokenResponse jwtTokenResponse = jwtUtils.generateToken(generateTokenDto);
        saveRefreshToken(jwtTokenResponse.getRefreshToken(), jwtTokenResponse.getRefreshTokenExpiresIn());

        return jwtTokenResponse;
    }


    public JwtTokenResponse generateTemporaryToken(GenerateTemporaryTokenDto generateTemporaryTokenDto) {

        return jwtUtils.generateTemporaryToken(generateTemporaryTokenDto);
    }


    public JwtTokenResponse regenerateAccessToken(GenerateTokenDto generateTokenDto) {

        return jwtUtils.regenerateAccessToken(generateTokenDto);
    }

    public RefreshTokenPayloadDto decodeRefreshToken(TokenRefreshRequest tokenRefreshRequest) {
        String refreshToken = tokenRefreshRequest.getRequestToken();

        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }
        if (refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken).isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        return jwtUtils.decodeRefreshToken(refreshToken);
    }

    private void saveRefreshToken(String refreshToken, Long expiresIn) {

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
        refreshTokenRepository.save(token);
    }


    private void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}