package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.annotation.OAuth2Provider;
import com.caring.caringbackend.api.internal.auth.dto.response.KakaoUserInfoResponse;
import com.caring.caringbackend.api.internal.auth.dto.response.OAuth2ProviderUserInfoResponse;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties;
import com.caring.caringbackend.domain.auth.properties.OAuth2ProviderProperties.ProviderProperties;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@OAuth2Provider("kakao")
@Service
public class KakaoOAuth2Service implements OAuth2Service {
    private final ProviderProperties properties;
    private final WebClient webClient;

    public KakaoOAuth2Service(OAuth2ProviderProperties properties, WebClient webClient) {
        this.properties = properties.getProviders().get("kakao");
        this.webClient = webClient;
    }

    @Override
    public OAuth2ProviderUserInfoResponse getUserInfoFromProvider(
            String accessToken) {
        return webClient.post()
                .uri(properties.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new BusinessException(ErrorCode.BAD_REQUEST)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new BusinessException(ErrorCode.EXTERNAL_API_ERROR)))
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }
}
