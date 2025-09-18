package com.caring.caringbackend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 🚨 에러 코드 정의 열거형
 *
 * 애플리케이션에서 발생할 수 있는 모든 오류 코드를 정의합니다.
 * 각 에러 코드는 HTTP 상태 코드, 고유 코드, 메시지를 포함합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Getter
public enum ErrorCode {

    // 🔧 Common Errors (COMMON-xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 내부 오류가 발생했습니다"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-403", "접근 권한이 없습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "요청한 리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON-405", "허용되지 않은 HTTP 메서드입니다"),
    CONFLICT(HttpStatus.CONFLICT, "COMMON-409", "리소스 충돌이 발생했습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "요청한 리소스를 찾을 수 없습니다"),

    // 📝 Validation Errors (VALID-xxx)
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALID-400", "입력값 검증에 실패했습니다"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "VALID-001", "필수 파라미터가 누락되었습니다"),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "VALID-002", "잘못된 형식입니다"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "VALID-003", "파라미터 타입이 올바르지 않습니다"),

    // 👤 User Domain Errors (USER-xxx)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER-002", "이미 존재하는 사용자입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER-003", "잘못된 비밀번호입니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER-004", "이미 사용 중인 이메일입니다"),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER-005", "이미 사용 중인 전화번호입니다"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "USER-006", "비활성화된 사용자입니다"),

    // 🔐 Authentication & Authorization Errors (AUTH-xxx)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-001", "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-002", "만료된 토큰입니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-003", "리프레시 토큰을 찾을 수 없습니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-004", "잘못된 인증 정보입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-005", "접근이 거부되었습니다"),

    // 🏥 Institution Domain Errors (INST-xxx)
    INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "INST-001", "기관을 찾을 수 없습니다"),
    INSTITUTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "INST-002", "이미 존재하는 기관입니다"),
    INSTITUTION_INACTIVE(HttpStatus.FORBIDDEN, "INST-003", "비활성화된 기관입니다"),

    // 📋 Care Domain Errors (CARE-xxx)
    CARE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "CARE-001", "케어 요청을 찾을 수 없습니다"),
    CARE_REQUEST_ALREADY_PROCESSED(HttpStatus.CONFLICT, "CARE-002", "이미 처리된 케어 요청입니다"),
    INVALID_CARE_STATUS(HttpStatus.BAD_REQUEST, "CARE-003", "유효하지 않은 케어 상태입니다"),

    // 📁 File Domain Errors (FILE-xxx)
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-001", "파일을 찾을 수 없습니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-002", "파일 업로드에 실패했습니다"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "FILE-003", "지원하지 않는 파일 형식입니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE-004", "파일 크기가 제한을 초과했습니다"),

    // 🗄️ Database Errors (DB-xxx)
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB-001", "데이터베이스 오류가 발생했습니다"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DB-002", "데이터 무결성 제약 조건을 위반했습니다"),
    OPTIMISTIC_LOCK_ERROR(HttpStatus.CONFLICT, "DB-003", "동시성 문제로 인한 충돌이 발생했습니다"),

    // 🌐 External API Errors (EXT-xxx)
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EXT-001", "외부 API 호출 중 오류가 발생했습니다"),
    EXTERNAL_API_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "EXT-002", "외부 API 호출 시간이 초과되었습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
