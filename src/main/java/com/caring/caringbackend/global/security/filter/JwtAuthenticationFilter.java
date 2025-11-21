package com.caring.caringbackend.global.security.filter;

import com.caring.caringbackend.global.security.exception.JwtAuthenticationEntryPoint;
import com.caring.caringbackend.global.security.exception.JwtAuthenticationException;
import com.caring.caringbackend.global.security.JwtUtils;
import com.caring.caringbackend.global.security.details.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = findAccessTokenFromHeader(request);
            if (accessToken == null) {
                accessToken = findAccessTokenFromCookie(request);
            }
            if (accessToken != null) {
                if (jwtUtils.isTokenExpired(accessToken)) {
                    throw new JwtAuthenticationException("Expired JWT token");
                }
                JwtUserDetails jwtUserDetails = jwtUtils.decodeJwtUserDetails(accessToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        jwtUserDetails,
                        null,
                        jwtUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            SecurityContextHolder.clearContext();
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static String findAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader( "Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private static String findAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
