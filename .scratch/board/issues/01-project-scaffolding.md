# 01 - 프로젝트 스캐폴딩 + 워킹 스켈레톤

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

`back/` 폴더에 Spring Boot 4.0.6 / JDK 25 / Gradle Kotlin DSL 프로젝트를 만들고, 부팅 가능한 워킹 스켈레톤을 구성한다. 앱이 기동되어 공통 레이아웃이 적용된 홈 페이지가 렌더되고, 프로파일별 H2 DB에 연결되며, 개발 모드에서 h2-console이 열리는 것까지가 한 슬라이스다. 이후 모든 기능 슬라이스의 토대.

- 루트 패키지 `com.back`, 메인 클래스 `com.back.BackApplication`.
- 의존성: DevTools, Lombok, Spring Data JPA, Validation, Spring Security, Thymeleaf, thymeleaf-layout-dialect, H2, commonmark-java, Jsoup.
- `@EnableJpaAuditing` 활성화, `BaseEntity`(@MappedSuperclass: `id` Long IDENTITY, `createDate` @CreatedDate, `modifyDate` @LastModifiedDate).
- 프로파일: 개발(`application.yml` + `application-dev.yml`, 파일 DB `./db_dev.mv.db`, `ddl-auto: update`, h2-console 활성), 테스트(`application.yml` + `application-test.yml`, 인메모리 H2, `ddl-auto: create`).
- 공통 레이아웃: thymeleaf-layout-dialect, Tailwind 4.x play CDN(`@tailwindcss/browser@4`), DaisyUI 5 CDN, Pretendard 다이나믹 서브셋 웹폰트. 레이아웃이 적용된 홈 페이지 1개.
- 이 단계의 Security는 최소 설정(스켈레톤이 떠야 함) — 상세 인증은 슬라이스 02에서.

## Acceptance criteria

- [ ] `back/`에서 dev 프로파일로 앱이 정상 기동된다.
- [ ] 공통 레이아웃(Tailwind/DaisyUI/Pretendard)이 적용된 홈 페이지가 렌더된다.
- [ ] dev 모드에서 파일 DB(`./db_dev.mv.db`)가 생성되고 h2-console에 접속된다.
- [ ] test 프로파일에서 인메모리 H2 + `ddl-auto: create`로 기동된다.
- [ ] `BaseEntity` 상속 엔티티에 `createDate`/`modifyDate`가 자동 기록된다(스모크 확인).

## Blocked by

None - can start immediately
