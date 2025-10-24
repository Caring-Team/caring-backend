package com.caring.caringbackend.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 📤 공통 API 응답 래퍼 클래스
 *
 * 모든 API 응답을 표준화된 형식으로 제공
 *
 * @param <T> 응답 데이터 타입
 * @author caring-team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 🎯 성공 여부
     */
    private boolean success;

    /**
     * 📝 응답 코드 (예: "OK", "USER-001")
     */
    private String code;

    /**
     * 💬 응답 메시지
     */
    private String message;

    /**
     * 📦 응답 데이터
     */
    private T data;

    /**
     * ✅ 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    /**
     * ✅ 성공 응답 생성 (데이터 없음)
     */
    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .success(true)
                .code("OK")
                .message("요청이 성공적으로 처리되었습니다.")
                .build();
    }

    /**
     * ✅ 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("OK")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * ❌ 실패 응답 생성
     */
    public static ApiResponse<Void> error(String code, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    /**
     * ❌ 실패 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
