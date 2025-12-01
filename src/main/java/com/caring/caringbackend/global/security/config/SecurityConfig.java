package com.caring.caringbackend.global.security.config;

import com.caring.caringbackend.global.security.exception.JwtAccessDeniedHandler;
import com.caring.caringbackend.global.security.exception.JwtAuthenticationEntryPoint;
import com.caring.caringbackend.global.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키/세션 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));
        http.
                sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS));
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(jwtAccessDeniedHandler);
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                });

        http
                .authorizeHttpRequests((requests) -> requests
                        // ========================================
                        // 1. Swagger 및 API 문서 (공개)
                        // ========================================
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ========================================
                        // 2. 인증 API (공개)
                        // ========================================
                        // 회원 인증
                        .requestMatchers(
                                "/api/v1/auth/certification-code",      // 회원 휴대폰 인증 코드 전송
                                "/api/v1/auth/verify-phone",            // 회원 휴대폰 번호 인증
                                "/api/v1/auth/login",                   // 회원 로컬 로그인
                                "/api/v1/auth/oauth2/login/**",         // 회원 OAuth2 로그인
                                "/api/v1/auth/token/refresh"            // 회원 토큰 갱신
                        ).permitAll()

                        // 기관 인증
                        .requestMatchers(
                                "/api/v1/auth/institution/certification-code",  // 기관 인증 코드 전송
                                "/api/v1/auth/institution/verify-phone",        // 기관 휴대폰 인증
                                "/api/v1/auth/institution/login",               // 기관 로그인
                                "/api/v1/auth/institution/token/refresh"        // 기관 토큰 갱신
                        ).permitAll()

                        // ========================================
                        // 3. 공개 API (인증 불필요)
                        // ========================================
                        .requestMatchers("/api/v1/public/**").permitAll()  // 공개 기관, 광고, 태그

                        // ========================================
                        // 4. 회원 전용 API (ROLE_USER)
                        // ========================================
                        .requestMatchers(
                                "/api/v1/member/**"
                        ).hasRole("USER")

                        // ========================================
                        // 5. 기관 관리자 전용 API (ROLE_INSTITUTION_OWNER, ROLE_INSTITUTION_STAFF)
                        // ========================================
                        .requestMatchers(
                                "/api/v1/institution/**"
                        ).hasAnyRole("INSTITUTION_OWNER", "INSTITUTION_STAFF")

                        // ========================================
                        // 6. 시스템 관리자 전용 API (ROLE_ADMIN)
                        // ========================================
                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        // ========================================
                        // 7. 테스트 경로 (개발 환경)
                        // ========================================
                        .requestMatchers("/api/v1/test/**").permitAll()

                        // ========================================
                        // 8. 그 외 모든 요청은 인증 필요
                        // ========================================
                        .anyRequest().authenticated());
        return http.build();
    }
}
