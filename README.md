# caring-backend

caring 백엔드 레포지토리입니다. 본 문서는 조직 레포지토리의 공통 규칙, 개발/배포 흐름, 인프라 개요, 환경변수/프로필 관리, 브랜치·이슈·PR 규칙, 코드 컨벤션, Swagger 계획을 정리합니다.

## 1) 인프라 및 배포 개요
- 기술 스택
  - Java 17, Spring Boot 3.5.x (Web, Data JPA), Lombok, PostgreSQL
- 프로필
  - 기본 활성 프로필: dev (src/main/resources/application.yml)
  - dev: 로컬 개발용, DDL auto=create, 8080 포트
  - prod: 운영용, DDL auto=update, 환경변수로 DB 접속정보 주입
- 컨테이너/이미지
  - Dockerfile: Gradle 멀티스테이지 빌드 → Temurin 17 JRE로 app.jar 실행, EXPOSE 8080
  - ECR 이미지: 430118840639.dkr.ecr.ap-northeast-2.amazonaws.com/caring-server:latest
- 런타임
  - docker-compose (서버): SPRING_PROFILES_ACTIVE=prod, 8080:8080 포트, /app/files 볼륨 마운트

### AWS 구성 요약
- ECR: 애플리케이션 도커 이미지 저장소(caring-server)
- EC2: 애플리케이션 컨테이너 실행 호스트
- RDS(PostgreSQL): 운영 DB

### 배포 파이프라인
- 흐름: develop 브랜치에 PR merge → main 브랜치로 push → GitHub Actions → 도커 이미지 빌드/푸시(ECR) → EC2에서 pull & 재기동
- 필요 시나리오

## 2) 환경변수 및 yml 관리
- 파일 구성
  - application.yml: 기본 프로필(dev) 지정
  - application-dev.yml: 로컬 개발 DB (jdbc:postgresql://localhost:5432/caring), ddl-auto=create
  - application-prod.yml: DATASOURCE_URL/USERNAME/PASSWORD 환경변수 사용, ddl-auto=update, show-sql=false

  - 비밀번호/키 등 비밀정보는 코드에 커밋 금지. GitHub Actions Secrets, EC2 환경변수 또는 .env 파일(docker-compose에서 참조) 사용 권장
  - 현재 dev yml에 비밀번호가 하드코딩되어 있으므로, 추후 환경변수로 전환 권장

## 3) 로컬 개발
- 요구사항: Java 17, Gradle(Wrapper 포함), PostgreSQL(로컬)
- 실행
  - 기본 dev 프로필 활성화로, `http://localhost:8080` 제공
  - DB: 로컬 Postgres에 caring 데이터베이스/계정 생성 후 application-dev.yml 정보에 맞추기
  - application-dev.yml 파일은 올리지 말아주세요!!

## 4) 브랜치 전략
- 기본 브랜치: main(배포), develop(통합)
- 작업 브랜치 규칙: [type]/branch생성날짜-#issue번호-짧은-설명
  - type: feat, fix, hotfix, refactor, chore, docs, test, ci, build, perf
  - 예: feat/250918-#123-사용자-회원가입-기능추가, fix/250918-#231-기관관리-기능추가

## 5) 이슈 규칙
- 제목: [type] 간단 요약 (#issueId 선택)
  - type: feat, fix, refactor, chore, docs, test, ci, build, perf, hotfix
  - 예: [feat] 회원가입 API 추가
- 본문 템플릿 권장
  - 배경/목표, 완료 조건(AC), 체크리스트, 영향 범위(스키마/배포), 스크린샷/참고자료
- 라벨: 영역(module), 우선순위(P0/1/2), 상태(TODO/DOING/REVIEW)

## 6) PR 규칙과 리뷰 규칙
- PR 대상: 본인기능브랜치 → develop, develop → main(릴리즈)
- 제목: [type](scope): PR 요약 제목 (#issue)
  - 예: [feat](auth): 회원가입 API 추가 (#123)
- 본문
    - [ ] 이슈 번호 연결
    - [ ] 기능/버그 수정 내용 요약
- 리뷰
  - 최소 1~2인 승인, 셀프-머지 금지(상황에 따라 유지보수자 승인 필요)
  - 변경 파일이 많으면 모듈/커밋 단위로 분리 제출
  - 성능/보안/호환성/테스트 누락 관점 리뷰
  - 최소 당일 24시간 내로 리뷰 후 merge 권장

## 8) 코드 컨벤션
- 언어/버전: Java 17, Spring Boot 3.5.x
- 스타일: 의존성 주입은 생성자 사용(@RequiredArgsConstructor), 필드 주입 금지
- 패키지 구조 MVC
  - controller → service → repository
- Null/Optional: NPE 방지 방어코드, Optional은 반환에 한정하여 사용
- 로깅: lombok @Slf4j 또는 LoggerFactory, System.out 금지,
- 예외: CustomException(에러코드 포함) 사용, ControllerAdvice로 일괄 처리

## 9) 주석 방식
- Javadoc 적극 사용: public 클래스/메서드, DTO, API 인터페이스에 문서화
- /* 
-  *이렇게 사용하시면 됩니다. 
-  *@param xxx 설명
-  *@return 설명
-  */
- 비즈니스 규칙/의도/경계조건은 코드 상단 블록 주석으로 명시
- TODO/FIXME: 추적 가능한 이슈 번호와 함께 사용

## 10) 계층 구조/폴더 구조
- 기준 패키지: `com.caring.caringbackend`
- 권장 구조
  - domain: 도메인별 패키지로 분리 
    - entity: JPA 엔티티
    - repository: JPA 리포지토리
    - service: 서비스/비즈니스 로직
  - global: 공통/전역 설정
    - config: 스프링 설정
    - exception: 예외 처리
    - util: 유틸리티 클래스
    - security: 시큐리티 설정
  - api 웹 계층
    - controller: REST 컨트롤러
    - dto: request/response DTO
    - infra: 외부 API 연동, 파일 저장 등

## 11) 사용할 의존성(현황)
- spring-boot-starter-web, spring-boot-starter-data-jpa
- lombok (compileOnly/annotationProcessor)
- postgresql (runtimeOnly)
- test: spring-boot-starter-test, junit-platform-launcher

## 12) Swagger(OpenAPI)
- 계획: springdoc-openapi 사용
  - 의존성 예: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x`
  - 설정 예: `/swagger-ui.html` 접근 허용, 프로필 별 노출 제어(dev만 노출 권장)
- 컨트롤러/DTO에 Javadoc/Swagger 어노테이션으로 필드/응답 문서화

---

문의/변경 제안은 이슈로 등록하고, 규칙 변경은 PR로 검토 후 본 문서에 반영합니다.
