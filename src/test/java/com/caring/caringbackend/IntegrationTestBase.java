package com.caring.caringbackend;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public abstract class IntegrationTestBase {

    /**
     * 로컬 PostgreSQL을 사용한 통합 테스트 환경 바인딩
     * - .env 혹은 환경변수에 TEST_DB_URL / TEST_DB_USERNAME / TEST_DB_PASSWORD 설정 시 우선 적용
     * - 미설정 시 localhost:5432/caring / postgres / postgres 기본값 사용
     */
    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        String url = getenvOrDefault("TEST_DB_URL", "jdbc:postgresql://localhost:5432/caringdb");
        String username = getenvOrDefault("TEST_DB_USERNAME", "postgres");
        String password = getenvOrDefault("TEST_DB_PASSWORD", "postgres");

        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> username);
        registry.add("spring.datasource.password", () -> password);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    private static String getenvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
        }
}

