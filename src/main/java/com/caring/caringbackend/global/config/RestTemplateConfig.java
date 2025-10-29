package com.caring.caringbackend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * RestTemplate 설정
 */
@Configuration
public class RestTemplateConfig {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Kakao API 인증 헤더 추가 인터셉터
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            if (request.getURI().toString().contains("dapi.kakao.com")) {
                request.getHeaders().set("Authorization", "KakaoAK " + kakaoApiKey);
            }
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
    }
}

