package com.caring.caringbackend.global.exception;

import com.caring.caringbackend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 🛡️ 글로벌 예외 처리 핸들러
 *
 * 모든 컨트롤러에서 발생하는 예외를 통합 처리합니다.
 * 공통 응답 형식으로 클라이언트에게 일관된 오류 정보를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 🎯 비즈니스 예외 처리
     *
     * @param ex 비즈니스 예외
     * @param request HTTP 요청
     * @return 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        ErrorCode errorCode = ex.getErrorCode();

        log.warn("BusinessException occurred: {} at {}",
                errorCode.getCode(), request.getRequestURI(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .data(ex.getData())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 📝 Bean Validation 예외 처리 (@Valid, @Validated)
     *
     * @param ex 검증 예외
     * @param request HTTP 요청
     * @return 검증 오류 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.debug("📝 Validation errors at {}: {}", request.getRequestURI(), errors);

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(errors)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 🔍 Constraint Violation 예외 처리
     *
     * @param ex 제약조건 위반 예외
     * @param request HTTP 요청
     * @return 제약조건 오류 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        log.debug("🔍 Constraint violations at {}: {}", request.getRequestURI(), errors);

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(errors)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 🔗 요청 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        log.debug("🔗 Missing parameter: {} at {}", ex.getParameterName(), request.getRequestURI());

        ErrorCode errorCode = ErrorCode.MISSING_REQUEST_PARAMETER;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(String.format("필수 파라미터가 누락되었습니다: %s", ex.getParameterName()))
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 🎭 파라미터 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.debug("🎭 Type mismatch: {} at {}", ex.getName(), request.getRequestURI());

        ErrorCode errorCode = ErrorCode.TYPE_MISMATCH;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(String.format("파라미터 타입이 올바르지 않습니다: %s", ex.getName()))
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 🚫 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.debug("🚫 Method not supported: {} at {}", ex.getMethod(), request.getRequestURI());

        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(String.format("지원하지 않는 HTTP 메서드입니다: %s", ex.getMethod()))
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 📂 리소스를 찾을 수 없는 예외 처리 (정적 리소스)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpServletRequest request) {

        // Swagger UI 관련 요청이나 favicon 등은 WARNING 레벨로 로깅하지 않음
        String uri = request.getRequestURI();
        if (uri.contains("swagger") || uri.contains("api-docs") || uri.contains("favicon")) {
            log.debug("📂 Static resource not found: {}", uri);
        } else {
            log.warn("📂 Resource not found: {}", uri);
        }

        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message("요청한 리소스를 찾을 수 없습니다")
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * 🗄️ 데이터 무결성 위반 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.warn("🗄️ Data integrity violation at {}", request.getRequestURI(), ex);

        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message("데이터 무결성 제약 조건을 위반했습니다")
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    /**
     * ⚠️ 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(
            Exception ex, HttpServletRequest request) {

        log.error("⚠️ Unexpected error at {}", request.getRequestURI(), ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .code(errorCode.getCode())
                .message("서버 내부 오류가 발생했습니다")
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
