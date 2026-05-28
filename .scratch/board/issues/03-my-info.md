# 03 - 내 정보 조회+수정

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

로그인한 회원이 "내 정보" 페이지에서 자신의 `username`·`nickname`을 조회하고, `nickname`과 `password`를 수정하는 경로 전체. `username`은 변경 불가. 탈퇴 기능은 없다.

- `nickname` 수정 시 타인과 중복이면 검증 오류.
- `password` 수정 시 BCrypt로 재인코딩하여 저장.

## Acceptance criteria

- [ ] 로그인 회원이 내 정보 페이지에서 본인 `username`·`nickname`을 본다.
- [ ] `nickname`을 수정하면 반영되고, 중복 시 검증 오류가 표시된다.
- [ ] `password`를 수정하면 BCrypt로 재인코딩되어 저장되고, 변경된 비밀번호로 재로그인된다.
- [ ] `username` 변경 입력 수단이 노출되지 않는다(불변).
- [ ] 비로그인 접근 시 로그인 페이지로 유도된다.

## Blocked by

- 02 - 회원가입·로그인·로그아웃
