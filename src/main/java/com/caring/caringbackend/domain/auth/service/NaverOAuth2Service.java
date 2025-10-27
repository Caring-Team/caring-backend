package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.annotation.OAuth2Provider;
import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.response.NaverTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.NaverUserInfoResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderUserInfoResponse;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties.ProviderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@OAuth2Provider("naver")
@Service
public class NaverOAuth2Service implements OAuth2Service {
    private final ProviderProperties properties;
    private final WebClient webClient;

    @Autowired
    public NaverOAuth2Service(OAuth2ProviderProperties properties, WebClient webClient) {
        this.properties = properties.getProviders().get("naver");
        this.webClient = webClient;
    }

    /*
    Type: POST

    Uri: https://nid.naver.com/oauth2.0/token
    {
        grant_type=authorization_code
        client_id={CLIENT_ID}
        client_secret={CLIENT_SECRET}
        code={AUTHORIZATION_CODE}
        state={STATE}

    }
     */
    @Override
    public OAuth2ProviderTokenResponse getTokenFromProvider(OAuthLoginRequest request) {
        return webClient.post().uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(tokenParams(request))
                .retrieve()
                // TODO: ON STATUS
                .bodyToMono(NaverTokenResponse.class)
                .block();
    }

    @Override
    public OAuth2ProviderUserInfoResponse getUserInfoFromProvider(
            OAuth2ProviderTokenResponse response) {
        System.out.println(response.getAccessToken());
        return webClient.post().uri(properties.getUserInfoUri())
                .header("Authorization", "Bearer " + response.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NaverUserInfoResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenParams(OAuthLoginRequest request) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", request.getAuthorizationCode());
        params.add("state", request.getState());
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        //params.add("redirect_uri", properties.getRedirectUri());
        params.add("grant_type", "authorization_code");
        return params;
    }
}
