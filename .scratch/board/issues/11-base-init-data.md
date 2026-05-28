# 11 - 샘플 데이터 (BaseInitData)

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

첫 기동 시 샘플 데이터를 생성하는 `BaseInitData`. `com.back.global.initData.BaseInitData`에서 `baseInitDataApplicationRunner` 빈을 만들고 `@Transactional`로 실행한다.

- 멱등: 회원이 1명이라도 있으면 시드 로직을 중단한다.
- 회원 5명(user1~user5: username/nickname/password, 비밀번호는 BCrypt 인코딩).
- 글 5개(작성자 분산).
- 댓글 5개(여러 글에 분산, 1단계 대댓글 1~2개 포함해 구조 검증).
- 추천·조회수는 시드하지 않는다(0에서 시작).

## Acceptance criteria

- [ ] 빈 DB로 기동하면 회원 5·글 5·댓글 5(대댓글 포함)가 생성된다.
- [ ] 회원이 이미 존재하면 재기동 시 시드가 다시 실행되지 않는다(멱등).
- [ ] 샘플 회원 비밀번호가 BCrypt로 저장되어 로그인 가능하다.
- [ ] 추천수·조회수는 0으로 시작한다.

## Blocked by

- 08 - 대댓글(1단계) + 소프트 삭제 분기
