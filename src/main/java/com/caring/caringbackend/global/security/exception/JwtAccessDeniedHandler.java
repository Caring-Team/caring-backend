package com.caring.caringbackend.global.security.exception;

import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message("권한이 없습니다.")
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
}
