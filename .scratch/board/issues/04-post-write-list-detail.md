# 04 - 글 작성·목록·상세 (마크다운 렌더)

Status: ready-for-agent

## Parent

`.scratch/board/PRD.md`

## What to build

회원이 마크다운으로 글을 작성하고, 누구나 글 목록과 상세를 보는 read/write 경로 전체. `Post` 엔티티(`title` 평문, `content` 마크다운 원본, `viewCount` 기본 0, 작성자 `Member` N:1), `PostService`(create/read), 그리고 깊은 모듈 `MarkdownRenderer`를 포함한다.

- `MarkdownRenderer.render(markdown)`: commonmark로 HTML 변환 → Jsoup 화이트리스트로 정화(XSS 차단). 상태/DB 없음. 상세에서 `th:utext`로 출력. (ADR 0001 준수)
- DB에는 마크다운 원본 저장, 렌더는 출력 시.
- 목록: 작성일 내림차순, 페이지당 10개(`Pageable`). 제목·작성자(`nickname`)·작성일·조회수·추천수 표시(추천수/조회수는 0으로 표시 가능).
- 작성은 로그인 필요, 목록·상세 조회는 공개.

## Acceptance criteria

- [ ] 로그인 회원이 제목+마크다운 본문으로 글을 작성하면 `Post`가 원본 마크다운으로 저장된다.
- [ ] 글 상세에서 마크다운이 정화된 HTML로 렌더된다(`th:utext`).
- [ ] `<script>` 등 위험 태그/속성이 정화로 제거된다.
- [ ] 목록이 최신순으로 페이지당 10개씩 페이징되어 표시된다.
- [ ] 목록에 제목·작성자·작성일·조회수·추천수가 노출된다.
- [ ] 비로그인은 목록/상세를 볼 수 있으나 작성은 로그인으로 유도된다.
- [ ] `MarkdownRenderer` 단위 테스트: 기본 문법 변환, 위험 태그 제거, 빈/평문 입력 처리.

## Blocked by

- 02 - 회원가입·로그인·로그아웃
