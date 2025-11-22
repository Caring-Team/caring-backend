package com.caring.caringbackend.global.integration.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI 서버 설정 Properties
 *
 * @author 나의찬
 * @since 2025-11-22
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ai.server")
public class AiServerProperties {

    /**
     * AI 서버 기본 URL
     */
    private String baseUrl;

    /**
     * 기관 임베딩 엔드포인트
     */
    private String institutionEmbeddingPath = "/api/v1/embeddings/institution";


    /**
     * 추천 엔드포인트
     */
    private String recommendationPath = "/api/v1/recommendations";
    /**
     * 연결 타임아웃 (밀리초)
     */
    private int connectTimeout = 5000;

    /**
     * 읽기 타임아웃 (밀리초)
     */
    private int readTimeout = 10000;

    /**
     * API 키 (선택)
     */
    private String apiKey;
}

