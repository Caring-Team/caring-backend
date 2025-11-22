package com.caring.caringbackend.global.integration.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * AI 서버 통신을 위한 RestTemplate 설정
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Configuration
@RequiredArgsConstructor
public class AiServerRestTemplateConfig {

    private final AiServerProperties aiServerProperties;

    /**
     * AI 서버 통신용 RestTemplate 빈 생성
     */
    @Bean(name = "aiServerRestTemplate")
    public RestTemplate aiServerRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(aiServerProperties.getBaseUrl())
                .connectTimeout(Duration.ofSeconds(aiServerProperties.getConnectTimeout()))
                .readTimeout(Duration.ofSeconds(aiServerProperties.getReadTimeout()))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(
                        new SimpleClientHttpRequestFactory()))
                .build();
    }
}

