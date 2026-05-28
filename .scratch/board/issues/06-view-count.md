# 06 - 글 조회수 (쿠키 중복제거)

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

글 상세 조회 시 조회수를 집계하되, 쿠키 기반 중복제거로 같은 날 같은 글 재조회는 증가시키지 않는 경로 전체. 깊은 모듈 `ViewCountCookiePolicy`를 포함한다.

- `ViewCountCookiePolicy`: 현재 쿠키값·postId·현재시각(`Clock` 주입)을 받아 (증가 여부, 갱신된 쿠키값·자정 만료)을 반환. 본 글 ID를 쿠키에 누적, 매일 자정 만료. 비로그인도 동작.
- 조회수 증가는 엔티티 dirty checking이 아닌 **별도 UPDATE 쿼리**로 처리해 `@LastModifiedDate`(`modifyDate`)가 갱신되지 않게 한다.

## Acceptance criteria

- [ ] 신규 글을 처음 열면 조회수가 1 증가한다.
- [ ] 같은 날 같은 글을 다시 열면 조회수가 증가하지 않는다.
- [ ] 자정 경과 후 다시 열면 조회수가 다시 증가한다.
- [ ] 비로그인 방문자에게도 중복제거가 적용된다.
- [ ] 조회수 증가 후에도 해당 글의 `modifyDate`가 변하지 않는다.
- [ ] `ViewCountCookiePolicy` 단위 테스트(`Clock` 주입): 첫 조회 증가, 동일일 미증가, 자정 경과 후 재증가, 여러 글 ID 누적/만료.

## Blocked by

- 04 - 글 작성·목록·상세 (마크다운 렌더)
