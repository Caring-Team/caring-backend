package com.caring.caringbackend.global.jwt.filter;

import com.caring.caringbackend.global.jwt.exception.JwtAuthenticationEntryPoint;
import com.caring.caringbackend.global.jwt.exception.JwtAuthenticationException;
import com.caring.caringbackend.global.jwt.JwtUtils;
import com.caring.caringbackend.global.jwt.details.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new JwtAuthenticationException("Could not find JWT token");
            }
            String accessToken = header.substring(7);
            if (jwtUtils.isTokenExpired(accessToken)) {
                throw new JwtAuthenticationException("Expired JWT token");
            }
            JwtUserDetails jwtUserDetails = jwtUtils.decodeJwtUserDetails(accessToken);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    jwtUserDetails,
                    null,
                    jwtUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
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
}
