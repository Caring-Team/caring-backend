package com.caring.caringbackend.global.security.config;

import com.caring.caringbackend.global.security.exception.JwtAccessDeniedHandler;
import com.caring.caringbackend.global.security.exception.JwtAuthenticationEntryPoint;
import com.caring.caringbackend.global.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
                        // Swagger 및 API 문서 경로
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // 회원 인증 경로
                        .requestMatchers("/api/v1/auth/oauth2/login/**").permitAll()
                        .requestMatchers("/api/v1/auth/token/refresh").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/certification-code").permitAll()
                        .requestMatchers("/api/v1/auth/verify-phone").permitAll()

                        // 기관 인증 경로
                        .requestMatchers("/api/v1/auth/institution/login").permitAll()
                        .requestMatchers("/api/v1/auth/institution/verify-phone").permitAll()
                        .requestMatchers("/api/v1/auth/institution/certification-code").permitAll()
                        .requestMatchers("/api/v1/auth/institution/token/refresh").permitAll()

                        // 공개 API (인증 불필요)
                        .requestMatchers(HttpMethod.GET, "/api/v1/institutions/profile/*").permitAll()  // 기관 상세 조회
                        .requestMatchers("/api/v1/institutions/*/reviews").permitAll()  // 기관 리뷰 목록 조회
                        .requestMatchers("/api/v1/reviews/*").permitAll()  // 리뷰 상세 조회
                        .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()  // 태그 조회만 공개
                        .requestMatchers("/api/v1/tags/**").hasRole("ADMIN")  // 태그 관리는 시스템 관리자만

                        // 테스트 경로 (개발 환경)
                        .requestMatchers("/api/v1/test/**").permitAll()

                        .anyRequest().authenticated());
        return http.build();
    }
}
