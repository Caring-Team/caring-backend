package com.caring.caringbackend.api.user.controller;

import com.caring.caringbackend.domain.test.entity.TestData;
import com.caring.caringbackend.domain.test.service.TestDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 🧪 테스트 컨트롤러
 *
 * 서버 상태 확인 및 기본 테스트를 위한 엔드포인트를 제공합니다.
 *
 * @author caring-team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Tag(name = "🧪 Test", description = "테스트 API")
public class TestController {

    private final TestDataService testDataService;

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

    /**
     * 📊 테스트 데이터 초기화
     */
    @Operation(
        summary = "테스트 데이터 초기화",
        description = "P6Spy 테스트를 위한 샘플 데이터를 생성합니다."
    )
    @GetMapping("/init")
    public String initTestData() {
        log.info("🔄 테스트 데이터 초기화 시작");

        // 기존 데이터 삭제
        testDataService.deleteAll();

        // 테스트 데이터 생성
        testDataService.save(TestData.builder()
                .name("홍길동")
                .description("P6Spy 테스트 데이터 1")
                .age(25)
                .email("hong@test.com")
                .build());

        testDataService.save(TestData.builder()
                .name("김철수")
                .description("P6Spy 테스트 데이터 2")
                .age(30)
                .email("kim@test.com")
                .build());

        testDataService.save(TestData.builder()
                .name("이영희")
                .description("P6Spy 테스트 데이터 3")
                .age(28)
                .email("lee@test.com")
                .build());

        testDataService.save(TestData.builder()
                .name("박민수")
                .description("P6Spy 테스트 데이터 4")
                .age(35)
                .email("park@test.com")
                .build());

        testDataService.save(TestData.builder()
                .name("최지은")
                .description("P6Spy 테스트 데이터 5")
                .age(27)
                .email("choi@test.com")
                .build());

        log.info("✅ 테스트 데이터 5개 생성 완료");
        return "✅ 테스트 데이터 5개 생성 완료";
    }

    /**
     * 📋 전체 테스트 데이터 조회
     */
    @Operation(
        summary = "전체 테스트 데이터 조회",
        description = "모든 테스트 데이터를 조회합니다. P6Spy로 SELECT 쿼리를 확인하세요."
    )
    @GetMapping("/data")
    public List<TestData> getAllTestData() {
        log.info("📋 전체 테스트 데이터 조회");
        return testDataService.findAll();
    }

    /**
     * 🔍 ID로 테스트 데이터 조회
     */
    @Operation(
        summary = "ID로 테스트 데이터 조회",
        description = "특정 ID의 테스트 데이터를 조회합니다. WHERE 절의 파라미터 바인딩을 확인하세요."
    )
    @GetMapping("/data/{id}")
    public TestData getTestDataById(@PathVariable Long id) {
        log.info("🔍 테스트 데이터 조회 - ID: {}", id);
        return testDataService.findById(id);
    }

    /**
     * 📧 이메일로 테스트 데이터 조회
     */
    @Operation(
        summary = "이메일로 테스트 데이터 조회",
        description = "이메일로 테스트 데이터를 조회합니다. 실제 이메일 값이 바인딩되는 것을 확인하세요."
    )
    @GetMapping("/data/email/{email}")
    public TestData getTestDataByEmail(@PathVariable String email) {
        log.info("📧 테스트 데이터 조회 - Email: {}", email);
        return testDataService.findByEmail(email);
    }

    /**
     * 🔎 이름으로 테스트 데이터 검색
     */
    @Operation(
        summary = "이름으로 테스트 데이터 검색",
        description = "이름으로 테스트 데이터를 검색합니다. LIKE 쿼리와 파라미터를 확인하세요."
    )
    @GetMapping("/data/search")
    public List<TestData> searchTestData(@RequestParam String name) {
        log.info("🔎 테스트 데이터 검색 - 이름: {}", name);
        return testDataService.searchByName(name);
    }

    /**
     * 🎂 나이 조건으로 테스트 데이터 조회
     */
    @Operation(
        summary = "나이 조건으로 조회",
        description = "특정 나이 이상의 테스트 데이터를 조회합니다. @Query의 파라미터 바인딩을 확인하세요."
    )
    @GetMapping("/data/age")
    public List<TestData> getTestDataByAge(@RequestParam Integer minAge) {
        log.info("🎂 테스트 데이터 조회 - 최소 나이: {}", minAge);
        return testDataService.findByAgeGreaterThan(minAge);
    }
}
