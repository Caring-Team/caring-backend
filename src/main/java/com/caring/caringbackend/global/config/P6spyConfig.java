package com.caring.caringbackend.global.config;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * P6Spy 설정 클래스
 * SQL 쿼리 로깅을 위한 커스텀 포맷터 설정
 */
@Configuration
public class P6spyConfig {

    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spyPrettySqlFormatter.class.getName());
    }

    /**
     * P6Spy 커스텀 로그 포맷터
     * - 실제 바인딩된 파라미터 포함
     * - SQL 포맷팅 (읽기 쉽게)
     * - 실행 시간 표시
     * - 느린 쿼리 경고
     */
    public static class P6spyPrettySqlFormatter implements MessageFormattingStrategy {

        // 느린 쿼리 임계값 (밀리초)
        private static final long SLOW_QUERY_THRESHOLD_MS = 500;

        @Override
        public String formatMessage(int connectionId, String now, long elapsed, String category,
                                     String prepared, String sql, String url) {
            // SQL이 없으면 빈 문자열 반환
            if (sql == null || sql.trim().isEmpty()) {
                return "";
            }

            // SQL 포맷팅 (Hibernate FormatStyle 사용)
            String formattedSql = formatSql(sql);

            // 실행 시간 경고 표시
            String timeWarning = elapsed > SLOW_QUERY_THRESHOLD_MS ? " ⚠️ SLOW QUERY" : "";

            // 로그 메시지 구성
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("=====================================[P6Spy SQL Logging]=====================================\n");
            sb.append(String.format("⏱️  실행 시간: %dms%s\n", elapsed, timeWarning));
            sb.append(String.format("🔗 Connection ID: %d\n", connectionId));
            sb.append(String.format("📅 실행 시각: %s\n", now));
            sb.append("===========================================================================================\n");
            sb.append(formattedSql);
            sb.append("\n===========================================================================================\n");

            return sb.toString();
        }

        /**
         * SQL 포맷팅
         * Hibernate의 FormatStyle을 사용하여 읽기 쉽게 변환
         */
        private String formatSql(String sql) {
            if (sql == null || sql.trim().isEmpty()) {
                return sql;
            }

            try {
                String formatted;
                // DML (SELECT, INSERT, UPDATE, DELETE)
                if (isDml(sql)) {
                    formatted = FormatStyle.BASIC.getFormatter().format(sql);
                }
                // DDL (CREATE, ALTER, DROP)
                else if (isDdl(sql)) {
                    formatted = FormatStyle.DDL.getFormatter().format(sql);
                }
                // 기타 SQL
                else {
                    formatted = sql;
                }

                // 왼쪽으로 한 탭(4칸)씩 이동
                return shiftLeft(formatted, 4);
            } catch (Exception e) {
                // 포맷팅 실패 시 원본 SQL 반환
                return sql;
            }
        }

        /**
         * 각 줄에서 왼쪽 공백을 지정된 개수만큼 제거
         */
        private String shiftLeft(String sql, int spaces) {
            if (sql == null) {
                return null;
            }

            String[] lines = sql.split("\n");
            StringBuilder result = new StringBuilder();

            for (String line : lines) {
                // 빈 줄은 건너뛰기
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 왼쪽에서 최대 spaces 개수만큼 공백 제거
                int spacesToRemove = 0;
                for (int i = 0; i < line.length() && i < spaces; i++) {
                    if (line.charAt(i) == ' ') {
                        spacesToRemove++;
                    } else {
                        break;
                    }
                }

                result.append(line.substring(spacesToRemove)).append("\n");
            }

            // 마지막 개행 제거
            if (result.length() > 0 && result.charAt(result.length() - 1) == '\n') {
                result.setLength(result.length() - 1);
            }

            return result.toString();
        }

        /**
         * DML 쿼리 여부 확인
         */
        private boolean isDml(String sql) {
            String sqlUpperCase = sql.trim().toUpperCase(Locale.ROOT);
            return sqlUpperCase.startsWith("SELECT") ||
                   sqlUpperCase.startsWith("INSERT") ||
                   sqlUpperCase.startsWith("UPDATE") ||
                   sqlUpperCase.startsWith("DELETE");
        }

        /**
         * DDL 쿼리 여부 확인
         */
        private boolean isDdl(String sql) {
            String sqlUpperCase = sql.trim().toUpperCase(Locale.ROOT);
            return sqlUpperCase.startsWith("CREATE") ||
                   sqlUpperCase.startsWith("ALTER") ||
                   sqlUpperCase.startsWith("DROP");
        }
    }
}
