package com.caring.caringbackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.mockito.Mockito.mock;

/**
 * 테스트 환경 설정
 */
@TestConfiguration
public class TestConfig {

    /**
     * 테스트용 Mock S3Client Bean
     */
    @Bean
    @Primary
    public S3Client s3Client() {
        return mock(S3Client.class);
    }

    /**
     * 테스트용 Mock S3Presigner Bean
     */
    @Bean
    @Primary
    public S3Presigner s3Presigner() {
        return mock(S3Presigner.class);
    }
}

