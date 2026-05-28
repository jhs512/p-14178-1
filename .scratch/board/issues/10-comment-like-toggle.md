# 10 - 댓글 추천 토글 (PostCommentLike)

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

회원이 댓글을 추천(토글)하고 댓글 추천수가 집계되는 경로 전체. `PostCommentLike` 엔티티(`Member` N:1 + `PostComment` N:1, 복합 유니크 제약)와 토글 서비스. 규칙은 글 추천(09)과 동일.

- 토글: 없으면 생성, 있으면 취소.
- 한 회원은 한 댓글에 1회만 추천(유니크 제약).
- 본인 댓글도 추천 가능.
- 댓글의 "추천수"가 PostCommentLike 개수로 표시된다.

## Acceptance criteria

- [ ] 로그인 회원이 댓글 추천을 누르면 추천수가 1 증가한다.
- [ ] 다시 누르면 취소되어 추천수가 감소한다.
- [ ] 같은 댓글을 두 번 추천할 수 없다(유니크 제약).
- [ ] 본인 댓글도 추천된다.
- [ ] 비로그인 추천 시 로그인으로 유도된다.

## Blocked by

- 07 - 댓글 작성·조회·수정·하드삭제 (평면)
