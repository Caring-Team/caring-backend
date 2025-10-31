package com.caring.caringbackend.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 통합 계층별 로깅 Aspect
 * Controller → Service → Repository → Service → Controller 전체 흐름 추적
 */
@Aspect
@Component
@Slf4j
public class LayerLoggingAspect {

    private static final long CONTROLLER_THRESHOLD_MS = 3000;
    private static final long SERVICE_THRESHOLD_MS = 2000;
    private static final long REPOSITORY_THRESHOLD_MS = 500;

    private static final ThreadLocal<Integer> CALL_DEPTH = ThreadLocal.withInitial(() -> 0);

    /**
     * Controller Layer
     */
    @Around("execution(* com.caring.caringbackend.api.controller..*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        // HTTP 요청 정보
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String httpMethod = "";
        String uri = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            httpMethod = request.getMethod();
            uri = request.getRequestURI();
        }

        int depth = CALL_DEPTH.get();
        CALL_DEPTH.set(depth + 1);

        long startTime = System.currentTimeMillis();
        String indent = getIndent(depth);

        // ===== 진입 로그 =====
        log.info("");
        log.info("{}╔════════════════════════════════════════════════════════════════════", indent);
        log.info("{}║ [Controller 진입] {}.{}()", indent, className, methodName);
        if (!uri.isEmpty()) {
            log.info("{}║    요청: {} {}", indent, httpMethod, uri);
        }
        if (joinPoint.getArgs().length > 0) {
            log.info("{}║    파라미터: {}", indent, formatArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // ===== 완료 로그 =====
            log.info("{}║ ✅ [Controller 완료] {}ms{}",
                indent,
                executionTime,
                executionTime > CONTROLLER_THRESHOLD_MS ? " ⚠️ SLOW!" : "");
            log.info("{}╚════════════════════════════════════════════════════════════════════", indent);

            if (executionTime > CONTROLLER_THRESHOLD_MS) {
                log.warn("{}⚠️ 느린 Controller 감지! (임계값: {}ms)", indent, CONTROLLER_THRESHOLD_MS);
            }

            return result;

        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("{}║ ❌ [Controller 실패] {}ms", indent, executionTime);
            log.error("{}║    예외: {} - {}", indent, e.getClass().getSimpleName(), e.getMessage());
            log.error("{}╚════════════════════════════════════════════════════════════════════", indent);
            throw e;

        } finally {
            CALL_DEPTH.set(depth);
        }
    }

    /**
     * Service Layer
     */
    @Around("execution(* com.caring.caringbackend.domain..service..*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        int depth = CALL_DEPTH.get();
        CALL_DEPTH.set(depth + 1);

        long startTime = System.currentTimeMillis();
        String indent = getIndent(depth);

        // ===== 진입 로그 =====
        log.debug("");
        log.debug("{}╔────────────────────────────────────────────────────────────────────", indent);
        log.debug("{}║ [Service 진입] {}.{}()", indent, className, methodName);
        if (joinPoint.getArgs().length > 0) {
            log.debug("{}║    파라미터: {}", indent, formatArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // ===== 완료 로그 =====
            if (result != null) {
                String resultStr = result.toString();
                if (resultStr.length() > 80) {
                    log.debug("{}║    반환값: {}...", indent, resultStr.substring(0, 80));
                } else {
                    log.debug("{}║    반환값: {}", indent, resultStr);
                }
            }
            log.debug("{}║ ✅ [Service 완료] {}ms{}",
                indent,
                executionTime,
                executionTime > SERVICE_THRESHOLD_MS ? " ⚠️ SLOW!" : "");
            log.debug("{}╚────────────────────────────────────────────────────────────────────", indent);

            if (executionTime > SERVICE_THRESHOLD_MS) {
                log.warn("{}⚠️ 느린 Service 감지! (임계값: {}ms)", indent, SERVICE_THRESHOLD_MS);
            }

            return result;

        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("{}║ ❌ [Service 실패] {}ms", indent, executionTime);
            log.error("{}║    예외: {} - {}", indent, e.getClass().getSimpleName(), e.getMessage());
            log.error("{}╚────────────────────────────────────────────────────────────────────", indent);
            throw e;

        } finally {
            CALL_DEPTH.set(depth);
        }
    }

    /**
     * Repository Layer
     */
    @Around("execution(* com.caring.caringbackend.domain..repository..*.*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        int depth = CALL_DEPTH.get();
        CALL_DEPTH.set(depth + 1);

        long startTime = System.currentTimeMillis();
        String indent = getIndent(depth);

        // ===== 진입 로그 =====
        log.info("");
        log.info("{}╔····································································", indent);
        log.info("{}║ [Repository 진입] {}.{}()", indent, className, methodName);
        if (joinPoint.getArgs().length > 0) {
            log.info("{}║    파라미터: {}", indent, formatArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // ===== 완료 로그 =====
            log.info("{}║ ✅ [Repository 완료] {}ms{}",
                indent,
                executionTime,
                executionTime > REPOSITORY_THRESHOLD_MS ? " ⚠️ SLOW!" : "");
            log.info("{}╚····································································", indent);

            if (executionTime > REPOSITORY_THRESHOLD_MS) {
                log.warn("{}⚠️ 느린 Repository 감지! 쿼리 최적화 필요 (임계값: {}ms)",
                    indent, REPOSITORY_THRESHOLD_MS);
            }

            return result;

        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("{}║ ❌ [Repository 실패] {}ms", indent, executionTime);
            log.error("{}║    예외: {} - {}", indent, e.getClass().getSimpleName(), e.getMessage());
            log.error("{}╚····································································", indent);
            throw e;

        } finally {
            CALL_DEPTH.set(depth);
        }
    }

    /**
     * 들여쓰기 생성
     */
    private String getIndent(int depth) {
        if (depth == 0) {
            return "";
        }
        return "  ".repeat(depth);
    }

    /**
     * 파라미터 포맷팅
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");

            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else {
                String argStr = arg.toString();
                if (argStr.length() > 50) {
                    sb.append(argStr, 0, 50).append("...");
                } else {
                    sb.append(argStr);
                }
            }
        }
        return sb.toString();
    }
}
