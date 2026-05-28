# 02 - 회원가입·로그인·로그아웃

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

방문자가 `username`/`password`/`nickname`으로 가입하고, 커스텀 폼으로 로그인/로그아웃하는 인증 경로 전체. `Member` 엔티티, `MemberService`(중복 검증·BCrypt 인코딩), `UserDetailsService` 구현, Spring Security 설정(세션 기반·커스텀 폼 로그인·공개/보호 경로·dev h2-console 예외)을 포함한다.

- `Member`: `username`(unique, 불변), `password`(BCrypt 해시), `nickname`(unique).
- 가입 시 `username`·`nickname` 중복이면 명확한 검증 오류.
- 공개: 회원가입·로그인·(추후) 글 조회. 보호: 인증 필요한 나머지.
- 역할(Role) 없음 — 인증 여부만 검사.

## Acceptance criteria

- [ ] 회원가입 페이지에서 가입하면 비밀번호가 BCrypt로 저장된 `Member`가 생성된다.
- [ ] 중복 `username` 또는 `nickname`으로 가입 시 검증 오류가 표시된다.
- [ ] 커스텀 로그인 페이지에서 `username`/`password`로 로그인하면 인증 세션이 생성된다.
- [ ] 로그아웃(POST) 시 세션이 종료된다.
- [ ] 보호 경로 비인증 접근 시 로그인 페이지로 유도된다.
- [ ] dev 모드 h2-console이 CSRF 예외 + sameOrigin 프레임으로 정상 동작한다.

## Blocked by

- 01 - 프로젝트 스캐폴딩 + 워킹 스켈레톤
