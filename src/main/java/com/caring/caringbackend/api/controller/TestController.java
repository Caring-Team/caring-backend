package com.caring.caringbackend.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🧪 테스트 컨트롤러
 *
 * 서버 상태 확인 및 기본 테스트를 위한 엔드포인트를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "🧪 Test", description = "서버 상태 확인 및 테스트 API")
public class TestController {

    /**
     * 🏃‍♂️ 서버 상태 테스트
     * @return 테스트 메시지
     */
    @Operation(
        summary = "서버 상태 확인",
        description = "서버가 정상적으로 동작하는지 확인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ 서버 정상 동작"),
        @ApiResponse(responseCode = "500", description = "❌ 서버 오류")
    })
    @GetMapping("")
    public String test() {
        return "🎉 Test  컨트롤러 성공";
    }
}
