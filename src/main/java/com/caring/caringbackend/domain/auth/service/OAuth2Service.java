package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderUserInfoResponse;

public interface OAuth2Service {

    public OAuth2ProviderTokenResponse getTokenFromProvider(OAuthLoginRequest request);

    public OAuth2ProviderUserInfoResponse getUserInfoFromProvider(
            OAuth2ProviderTokenResponse response);
}
