# PRD: 커뮤니티 게시판 (Board)

Status: ready-for-agent

`back` 폴더에 자프링(Spring Boot 4.0.6, JDK 25, Gradle Kotlin DSL) 기반 커뮤니티 게시판을 구축한다. 루트 패키지 `com.back`, 메인 클래스 `com.back.BackApplication`. 용어는 `CONTEXT.md` 글로서리를, 마크다운 렌더링은 `docs/adr/0001-markdown-rendering.md`를 따른다.

## Problem Statement

회원이 글을 쓰고, 서로의 글과 댓글에 댓글·대댓글을 달고, 추천하고, 조회수를 확인할 수 있는 기본 커뮤니티 공간이 없다. 또한 학습/시연 목적의 자프링 표준 셋업(프로파일 분리, 샘플 데이터, 인증)이 한 번에 갖춰진 베이스가 필요하다.

## Solution

회원 가입·로그인·내 정보 관리, 글 CRUD, 댓글/대댓글 CRUD, 글·댓글 추천(토글), 글 조회수 집계를 제공하는 Thymeleaf 기반 서버 렌더링 웹앱을 만든다. 글 본문은 마크다운으로 작성하고 서버에서 안전하게 HTML로 변환·정화해 보여준다. 개발/테스트 프로파일을 분리하고, 첫 기동 시 샘플 데이터를 자동 생성한다.

## User Stories

1. 방문자로서, 로그인 없이 글 목록을 최신순으로 볼 수 있어, 어떤 글이 있는지 둘러볼 수 있다.
2. 방문자로서, 로그인 없이 글 상세와 댓글을 볼 수 있어, 가입 전에 내용을 확인할 수 있다.
3. 방문자로서, `username`/`password`/`nickname`으로 회원가입할 수 있어, 글과 댓글을 작성할 자격을 얻는다.
4. 방문자로서, 이미 사용 중인 `username` 또는 `nickname`으로 가입 시 명확한 오류를 받아, 중복을 피해 다시 입력할 수 있다.
5. 회원으로서, `username`/`password`로 로그인할 수 있어, 인증이 필요한 기능을 사용할 수 있다.
6. 회원으로서, 로그아웃할 수 있어, 공용 기기에서 내 세션을 종료할 수 있다.
7. 회원으로서, "내 정보"에서 내 `username`·`nickname`을 조회할 수 있어, 내 계정 상태를 확인할 수 있다.
8. 회원으로서, "내 정보"에서 `nickname`과 `password`를 수정할 수 있어, 표시명과 비밀번호를 갱신할 수 있다. (`username`은 변경 불가)
9. 회원으로서, 새 `nickname`이 타인과 겹치면 오류를 받아, 유일한 표시명을 유지할 수 있다.
10. 회원으로서, 마크다운으로 글(제목+본문)을 작성할 수 있어, 서식 있는 글을 올릴 수 있다.
11. 회원으로서, 글 상세에서 마크다운이 안전한 HTML로 렌더된 모습을 볼 수 있어, 작성 결과를 확인할 수 있다.
12. 작성자로서, 내 글을 수정할 수 있어, 내용을 갱신할 수 있다.
13. 작성자로서, 내 글을 삭제할 수 있어, 더 이상 노출하지 않을 수 있다.
14. 회원으로서, 타인의 글을 수정·삭제하려 하면 거부당해, 소유권이 보호된다.
15. 방문자로서, 글 목록에서 제목·작성자(`nickname`)·작성일·조회수·추천수를 한눈에 볼 수 있어, 인기 글을 가늠할 수 있다.
16. 방문자로서, 글 목록이 페이지당 10개로 페이징되어, 많은 글도 부담 없이 탐색할 수 있다.
17. 회원으로서, 글에 댓글을 달 수 있어, 글에 반응할 수 있다.
18. 회원으로서, 특정 최상위 댓글에 대댓글(1단계)을 달 수 있어, 그 댓글에 답할 수 있다.
19. 회원으로서, 대댓글에는 다시 답글을 달 수 없어(2단계 금지), 댓글 깊이가 일관되게 유지된다.
20. 작성자로서, 내 댓글을 수정할 수 있어, 오타나 내용을 고칠 수 있다.
21. 작성자로서, 대댓글이 없는 내 댓글을 삭제하면 완전히 사라져, 흔적이 남지 않는다.
22. 작성자로서, 대댓글이 달린 내 댓글을 삭제하면 "삭제된 댓글입니다"로 표시되고 대댓글은 보존되어, 대화 맥락이 깨지지 않는다.
23. 회원으로서, 타인의 댓글을 수정·삭제하려 하면 거부당해, 소유권이 보호된다.
24. 회원으로서, 글을 추천(토글)할 수 있어, 좋은 글에 추천을 표할 수 있다.
25. 회원으로서, 이미 추천한 글을 다시 누르면 추천이 취소되어, 마음을 바꿀 수 있다.
26. 회원으로서, 같은 글을 두 번 추천할 수 없어, 추천수가 정직하게 집계된다.
27. 회원으로서, 내 글도 추천할 수 있어, 별도 제약 없이 동작한다.
28. 회원으로서, 댓글도 글과 동일하게 추천(토글, 1회 제한, 본인 허용)할 수 있다.
29. 방문자로서, 글을 조회하면 조회수가 1 증가해, 글의 인기를 반영한다.
30. 방문자로서, 같은 글을 같은 날 다시 열어도 조회수가 더 오르지 않아, 새로고침 뻥튀기가 방지된다(쿠키, 자정 만료).
31. 비로그인 방문자로서도 조회수 중복제거가 적용되어, 일관된 집계가 이뤄진다.
32. 작성자로서, 내 글을 조회해 조회수가 올라도 글의 수정일(`modifyDate`)은 바뀌지 않아, 목록 정렬이 왜곡되지 않는다.
33. 개발자로서, 개발 모드에서 파일 DB(`./db_dev.mv.db`)와 h2-console을 사용할 수 있어, 데이터를 점검할 수 있다.
34. 개발자로서, 테스트 모드에서 인메모리 H2와 `ddl-auto: create`가 적용되어, 깨끗한 상태에서 테스트가 돈다.
35. 개발자로서, 회원이 한 명도 없을 때만 샘플 데이터(회원 5·글 5·댓글 5)가 생성되어, 재기동 시 중복 시드가 없다.

## Implementation Decisions

### 프로젝트 셋업
- 위치 `back/`, Spring Boot 4.0.6, JDK 25, Gradle Kotlin DSL(`build.gradle.kts`).
- 루트 패키지 `com.back`, 메인 클래스 `com.back.BackApplication`.
- 의존성: DevTools, Lombok, Spring Data JPA, Validation, Spring Security, Thymeleaf, thymeleaf-layout-dialect, H2, commonmark-java, Jsoup(또는 OWASP Java HTML Sanitizer).
- `@EnableJpaAuditing` 활성화.

### 프로파일
- 개발: `application.yml` + `application-dev.yml`, 파일 DB `./db_dev.mv.db`, `ddl-auto: update`, h2-console 활성화.
- 테스트: `application.yml` + `application-test.yml`, H2 인메모리, `ddl-auto: create`.

### 도메인 / 스키마
- `BaseEntity`(@MappedSuperclass): `id`(Long, IDENTITY), `createDate`(@CreatedDate), `modifyDate`(@LastModifiedDate). 모든 엔티티가 상속.
- `Member`: `username`(unique, 불변), `password`(BCrypt 해시 저장), `nickname`(unique).
- `Post`: `title`(평문), `content`(마크다운 원본), `viewCount`(long, 기본 0), 작성자 `Member`(N:1).
- `PostComment`: `content`(평문), 소속 `Post`(N:1), 작성자 `Member`(N:1), `parent`(self N:1, nullable — 1단계 대댓글), `isDeleted`(boolean, 소프트 삭제 플래그).
- `PostLike`: `Member`(N:1) + `Post`(N:1), 복합 유니크 제약(member_id, post_id).
- `PostCommentLike`: `Member`(N:1) + `PostComment`(N:1), 복합 유니크 제약(member_id, post_comment_id).

### 모듈 / 인터페이스
- `MarkdownRenderer`(깊은 모듈): `render(markdown: String): String` — commonmark로 HTML 변환 후 Jsoup 화이트리스트로 정화. 상태/DB 없음. 출력 시 `th:utext`로 사용.
- `ViewCountCookiePolicy`(깊은 모듈): 현재 쿠키값·postId·현재시각(`Clock` 주입)을 입력받아 (증가 여부, 갱신된 쿠키값·자정 만료)을 반환. 쿠키에 본 글 ID 누적, 매일 자정 만료.
- `MemberService`: 가입(username/nickname 중복 검증, 비밀번호 BCrypt 인코딩), 내 정보 수정(nickname 중복 검증, password 재인코딩), `UserDetailsService` 구현.
- `PostService`: 글 CRUD, 작성자 검증, 조회수 증가는 별도 UPDATE 쿼리로 처리해 `modifyDate` 미갱신.
- `PostCommentService`: 댓글/대댓글 CRUD, 삭제 시 대댓글 유무로 소프트(`isDeleted=true`)/하드 삭제 분기, 대댓글 부모는 최상위 댓글로 제한.
- `PostLikeService`·`PostCommentLikeService`: 토글(존재하면 삭제, 없으면 생성), 유니크 제약으로 중복 방지, 본인 추천 허용.
- 추천수·조회수 노출: 추천수는 우선 연관 count로 파생(목록 N+1 우려 시 추후 비정규화 고려 — 되돌리기 쉬워 PRD 비결정).

### 컨트롤러 / 화면 (Thymeleaf MVC, 서버 렌더)
- 레이아웃: thymeleaf-layout-dialect 공통 레이아웃. Tailwind 4.x play CDN(`@tailwindcss/browser@4`), DaisyUI 5 CDN, Pretendard 다이나믹 서브셋 웹폰트.
- 페이지: 회원가입, 로그인(커스텀 폼), 내 정보(조회+수정), 글 목록(최신순·10개 페이징), 글 상세(마크다운 렌더·조회수·추천·댓글/대댓글), 글 작성/수정, 댓글/대댓글 작성·수정·삭제, 추천 토글.

### 보안 (Spring Security)
- 커스텀 폼 로그인 페이지 + 세션 기반 인증, 비밀번호 BCrypt.
- 공개: 글 목록·상세·댓글 조회, 회원가입, 로그인. 보호(로그인 필요): 글/댓글 작성·수정·삭제, 추천 토글, 내 정보.
- 역할(Role) 없음 — 인증 여부만 검사. 소유권(작성자 본인)은 서비스 레벨에서 검증.
- 개발 모드 h2-console: 해당 경로 CSRF 예외 + `frameOptions(sameOrigin)`.

### 샘플 데이터
- `com.back.global.initData.BaseInitData`에서 `baseInitDataApplicationRunner` 빈 생성, `@Transactional`.
- 회원이 1명이라도 있으면 시드 중단(멱등).
- 회원 5(user1~user5), 글 5(작성자 분산), 댓글 5(여러 글에 분산, 대댓글 1~2개 포함). 추천·조회수는 시드하지 않음(0).

## Testing Decisions

좋은 테스트는 모듈의 **외부 동작(계약)** 만 검증하고 내부 구현에 결합하지 않는다. 같은 입력에 같은 출력/관찰 가능한 상태 변화를 확인한다.

- `MarkdownRenderer` (단위): 마크다운 기본 문법이 HTML로 변환되는지, `<script>`·`onerror` 등 위험 태그/속성이 정화로 제거되는지(XSS), 일반 텍스트·빈 입력 처리. DB·스프링 컨텍스트 불필요.
- `ViewCountCookiePolicy` (단위, `Clock` 주입): 신규 글 첫 조회 시 증가, 같은 날 재조회 시 미증가, 자정 경과 후 재조회 시 다시 증가, 쿠키에 여러 글 ID 누적/만료. 시간 의존을 `Clock`으로 고정.
- `PostComment` 삭제 분기 (통합, 테스트 프로파일 인메모리 H2): 대댓글 없는 댓글 삭제 시 행이 사라지는지(하드), 대댓글 있는 댓글 삭제 시 `isDeleted=true`로 남고 대댓글이 보존되는지(소프트).
- 프로토타입/프리어트: 그린필드라 기존 테스트 없음. 위 단위 테스트가 이후 테스트 컨벤션의 본보기가 된다. 통합 테스트는 `application-test.yml`(H2 인메모리, `ddl-auto: create`)에서 실행.
- Like 토글은 이번 PRD에서 자동화 테스트 대상 제외(수동 확인).

## Out of Scope

- 회원 탈퇴, `username` 변경.
- 관리자/역할(Role), 권한 등급.
- 2단계 이상 중첩 대댓글, 댓글 마크다운.
- 글 검색, 카테고리/태그, 첨부파일/이미지 업로드.
- 추천·조회수 샘플 시드.
- 비정규화 카운트 캐싱, 조회수 동시성 정밀 제어(되돌리기 쉬워 구현 단계 판단).
- 이메일 인증, 비밀번호 재설정, OAuth/소셜 로그인.

## Further Notes

- 용어는 `CONTEXT.md` 글로서리(Member, Post, PostComment, 대댓글, 삭제된 댓글, PostLike, PostCommentLike, 마크다운, viewCount)를 일관되게 사용.
- 마크다운 렌더링 접근은 `docs/adr/0001-markdown-rendering.md`(서버 commonmark + Jsoup 정화, 원본 저장)를 준수.
- DB에는 항상 마크다운 원본을 저장 — 렌더링 정책 변경 시 마이그레이션 불필요.
- 조회수 증가는 엔티티 dirty checking이 아닌 별도 UPDATE로 처리해 `@LastModifiedDate`가 갱신되지 않도록 한다.
