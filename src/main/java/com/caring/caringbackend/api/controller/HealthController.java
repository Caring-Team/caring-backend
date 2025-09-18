package com.caring.caringbackend.api.controller;

import com.caring.caringbackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔍 헬스체크 컨트롤러
 *
 * 서버 상태 확인 및 기본 정보 제공
 *
 * @author caring-team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "🔍 Health Check", description = "서버 상태 확인 API")
public class HealthController {

    @Value("${spring.application.name:caring-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:unknown}")
    private String activeProfile;

    /**
     * 🎯 기본 헬스체크
     */
    @Operation(
        summary = "🎯 서버 상태 확인",
        description = "서버가 정상적으로 실행 중인지 확인합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "✅ 서버 정상 동작")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.info("🔍 Health check requested");

        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("application", applicationName);
        healthInfo.put("profile", activeProfile);
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("message", "🎉 Caring Backend is running smoothly!");

        return ResponseEntity.ok(ApiResponse.success("서버가 정상적으로 동작 중입니다.", healthInfo));
    }

    /**
     * 📊 상세 시스템 정보
     */
    @Operation(
        summary = "📊 시스템 정보 조회",
        description = "서버의 상세 시스템 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "✅ 시스템 정보 조회 성공")
    })
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        log.info("📊 System info requested");

        Runtime runtime = Runtime.getRuntime();

        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("application", applicationName);
        systemInfo.put("profile", activeProfile);
        systemInfo.put("timestamp", LocalDateTime.now());

        // JVM 정보
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("jvmName", System.getProperty("java.vm.name"));
        jvmInfo.put("osName", System.getProperty("os.name"));
        jvmInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("jvm", jvmInfo);

        // 메모리 정보
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
        memoryInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
        memoryInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
        memoryInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        systemInfo.put("memory", memoryInfo);

        return ResponseEntity.ok(ApiResponse.success("시스템 정보 조회 완료", systemInfo));
    }

    /**
     * 📏 바이트를 읽기 쉬운 형태로 변환
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), pre);
    }
}
