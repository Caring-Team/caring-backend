package com.caring.caringbackend.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
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
     *
     * ⚠️ 태그 정렬: application.yml의 tags-sorter: alpha로 태그 번호 정렬
     * ⚠️ Operation 정렬: /static/swagger-custom.js에서 summary 번호로 정렬
     */
    @Bean
    public OpenAPI caringOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .addSecurityItem(securityRequirement())
                .components(securitySchemes())
                .tags(tagList());
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

    /**
     * 🏷️ API 태그 목록 (순서대로 표시)
     *
     * 📌 태그 그룹 구조:
     * 1. 🔐 인증 (Auth)
     * 2. 👤 회원 (Member)
     * 3. 🏥 기관 (Institution)
     * 4. 🔓 공개 API (Public)
     * 5. 🔧 관리자 (Admin)
     */
    private List<Tag> tagList() {
        return List.of(
                // ============================================
                // 🔐 인증 그룹 (Authentication)
                // ============================================
                new Tag()
                        .name("01. 🧑‍🤝‍🧑 Member Auth")
                        .description("회원 인증 API | 회원가입, 로그인, 토큰 관리"),

                new Tag()
                        .name("02. 🏥 Institution Auth")
                        .description("기관 인증 API | 기관 회원가입, 로그인, 토큰 관리"),

                // ============================================
                // 👤 회원 그룹 (Member Services)
                // ============================================
                new Tag()
                        .name("03. 👤 Member")
                        .description("회원 프로필 관리 API | 내 정보 조회/수정, 선호 태그 관리"),

                new Tag()
                        .name("04. 👵 Elderly Profile")
                        .description("어르신 프로필 관리 API | 어르신 등록/수정/삭제, 케어 정보 관리"),

                new Tag()
                        .name("05. 🧑‍🤝‍🧑 Member Reservation")
                        .description("회원 예약 관리 API | 예약 생성/조회/취소"),

                new Tag()
                        .name("06. 💬 Member Chat")
                        .description("회원 채팅 API | 기관과의 실시간 상담 채팅"),

                new Tag()
                        .name("07. 💬 Member Consult Requests")
                        .description("회원 상담 내역 API | 상담 요청 내역 조회"),

                new Tag()
                        .name("08. ⭐ Member Review")
                        .description("리뷰 관리 API | 리뷰 작성/수정/삭제/신고"),

                new Tag()
                        .name("09. 🤖 AI Recommendation")
                        .description("AI 추천 API | AI 기반 맞춤 기관 추천"),

                // ============================================
                // 🏥 기관 그룹 (Institution Services)
                // ============================================
                new Tag()
                        .name("10. 🏥 Institution Profile")
                        .description("기관 프로필 관리 API | 내 기관 정보 조회/수정, 태그 관리"),

                new Tag()
                        .name("11. 👩‍⚕️ Institution CareGiver")
                        .description("기관 요양보호사 관리 API | 요양보호사 등록/수정/삭제"),

                new Tag()
                        .name("12. 💬 Institution Counsel")
                        .description("기관 상담 서비스 관리 API | 상담 서비스 등록/수정/삭제, 시간 관리"),

                new Tag()
                        .name("13. 📺 Institution Advertisement")
                        .description("기관 광고 관리 API | 광고 신청/조회/수정"),

                new Tag()
                        .name("14. 🏥 Institution Reservation")
                        .description("기관 예약 관리 API | 예약 조회/상태 변경"),

                new Tag()
                        .name("15. 🏥 Institution Chat")
                        .description("기관 채팅 API | 회원과의 실시간 상담 채팅"),

                new Tag()
                        .name("16. 🏥 Institution Consult Requests")
                        .description("기관 상담 내역 API | 상담 요청 내역 조회"),

                // ============================================
                // 🔓 공개 API 그룹 (Public APIs - 인증 불필요)
                // ============================================
                new Tag()
                        .name("17. 🏥 Public Institution")
                        .description("공개 기관 API | 기관 검색/조회 (인증 불필요)"),

                new Tag()
                        .name("18. 📺 Public Advertisement")
                        .description("공개 광고 API | 광고 조회 (인증 불필요)"),

                new Tag()
                        .name("19. 🏷 Public Tag")
                        .description("공개 태그 API | 태그 조회 (인증 불필요)"),

                // ============================================
                // 🔧 관리자 그룹 (Admin Management)
                // ============================================
                new Tag()
                        .name("20. Admin Institution")
                        .description("관리자 기관 관리 API | 기관 승인/거절/조회"),

                new Tag()
                        .name("21. Admin Advertisement")
                        .description("관리자 광고 관리 API | 광고 심사/승인/거절"),

                new Tag()
                        .name("22. Admin Member")
                        .description("관리자 회원 관리 API | 회원 조회/관리"),

                new Tag()
                        .name("23. Admin Tag")
                        .description("관리자 태그 관리 API | 태그 생성/수정/삭제/활성화")
        );
    }
}
