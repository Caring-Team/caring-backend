package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.api.internal.auth.dto.request.user.oauth.UserOAuth2LoginRequest;
import com.caring.caringbackend.api.internal.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.api.internal.auth.dto.response.OAuth2ProviderUserInfoResponse;

public interface OAuth2Service {

    public OAuth2ProviderUserInfoResponse getUserInfoFromProvider(
            String accessToken);
}
