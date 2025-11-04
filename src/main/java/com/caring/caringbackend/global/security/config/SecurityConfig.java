package com.caring.caringbackend.global.security.config;

import com.caring.caringbackend.global.security.exception.JwtAccessDeniedHandler;
import com.caring.caringbackend.global.security.exception.JwtAuthenticationEntryPoint;
import com.caring.caringbackend.global.security.filter.JwtAuthenticationFilter;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
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
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()

                        .requestMatchers("/api/v1/auth/oauth2/login/**").anonymous()
                        .requestMatchers("/api/v1/auth/token/refresh").anonymous()
                        .requestMatchers("/api/v1/auth/login").anonymous()
                        .requestMatchers("/api/v1/auth/certification-code").anonymous()
                        .requestMatchers("/api/v1/auth/verify-phone").anonymous()

                        .requestMatchers("api/v1/auth/institution/login").anonymous()
                        .requestMatchers("api/v1/auth/institution/verify-phone").anonymous()
                        .requestMatchers("api/v1/auth/institution/certification-code").anonymous()
                        .requestMatchers("api/v1/auth/institution/token/refresh").anonymous()
                        .anyRequest().authenticated());
        return http.build();
    }
}
