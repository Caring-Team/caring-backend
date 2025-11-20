package com.caring.caringbackend.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 📚 Swagger/OpenAPI 설정
 *
 * API 문서화를 위한 Swagger UI 설정
 *
 * @author caring-team
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 📖 OpenAPI 설정 빈
     */
    @Bean
    public OpenAPI caringOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .addSecurityItem(securityRequirement())
                .components(securitySchemes());
    }

    /**
     * ℹ️ API 정보 설정
     */
    private Info apiInfo() {
        return new Info()
                .title("🏥 Caring Backend API")
                .description("""
                        **Caring 플랫폼 백엔드 API 문서**
                        
                        🎯 **주요 기능**
                        - 👤 사용자 관리 (회원가입, 로그인, 프로필)
                        - 🏥 기관 관리 (기관 등록, 정보 관리)
                        - 📋 케어 서비스 관리
                        - 🔒 인증/인가 시스템
                        
                        📱 **환경 정보**
                        - 🔧 개발 환경: dev 프로필
                        - 🗄️ 데이터베이스: PostgreSQL
                        - ☁️ 배포: AWS ECR + EC2
                        
                        """)
                .version("v1.0.0")
                .contact(contact())
                .license(license());
    }

    /**
     * 👥 연락처 정보
     */
    private Contact contact() {
        return new Contact()
                .name("Caring Team")
                .email("caring-team@example.com")
                .url("https://github.com/caring-team");
    }

    /**
     * 📜 라이선스 정보
     */
    private License license() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * 🌐 서버 목록
     */
    private List<Server> serverList() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("🔧 로컬 개발 서버");

        Server devServer = new Server()
                .url("http://43.203.41.246:8080")
                .description("🚀 개발 서버 (EC2)");

        return List.of(localServer, devServer);
    }

    /**
     * 🔒 보안 요구사항
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement()
                .addList("bearerAuth");
    }

    /**
     * 🔐 보안 스키마 설정
     */
    private Components securitySchemes() {
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("🔑 JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.");

        return new Components()
                .addSecuritySchemes("bearerAuth", bearerAuth);
    }
}
