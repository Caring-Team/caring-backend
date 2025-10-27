package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.annotation.OAuth2Provider;
import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.response.GoogleTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.GoogleUserInfoResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderUserInfoResponse;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties.ProviderProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@OAuth2Provider("google")
@Service
public class GoogleOAuth2Service implements OAuth2Service {
    private final ProviderProperties properties;
    private final WebClient webClient;

    public GoogleOAuth2Service(OAuth2ProviderProperties properties, WebClient webClient) {
        this.properties = properties.getProviders().get("google");
        this.webClient = webClient;
    }

    /*
    Type: POST

    Uri: https://oauth2.googleapis.com/token
        ?code={AUTHORIZATION_CODE}
        &client_id={CLIENT_ID}
        &redirect_uri={REDIRECT_URI}
        &grant_type=authorization_code
        &client_secret={CLIENT_SECRET}
     */
    @Override
    public OAuth2ProviderTokenResponse getTokenFromProvider(OAuthLoginRequest request) {
        return webClient.post().uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(tokenParams(request))
                .retrieve()
                // TODO: ON STATUS
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    /*
    Type: GET

    Uri: https://www.googleapis.com/oauth2/v3/userinfo

    Headers: {
        Authorization: Bearer {ACCESS_TOKEN}
    }
     */
    @Override
    public OAuth2ProviderUserInfoResponse getUserInfoFromProvider(
            OAuth2ProviderTokenResponse response) {
        return webClient.get().uri(properties.getUserInfoUri())
                .header("Authorization", "Bearer " + response.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GoogleUserInfoResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenParams(OAuthLoginRequest request) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", request.getAuthorizationCode());
        params.add("state", request.getState());
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("grant_type", "authorization_code");
        return params;
    }
}
