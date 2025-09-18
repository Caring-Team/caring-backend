package com.caring.caringbackend.global.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🌐 웹 설정 (개발 환경용)
 *
 * 개발 환경에서의 CORS 설정 등을 관리
 *
 * @author caring-team
 */
@Configuration
@Profile("dev")
public class WebConfig implements WebMvcConfigurer {

    /**
     * 🔓 CORS 설정 (개발 환경용)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
