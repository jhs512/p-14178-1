# 글 본문 마크다운: 서버 렌더링 + 정화

글(`Post`) 본문은 마크다운 원본으로 저장하고, 화면 출력 시 서버에서 commonmark-java로 HTML로 변환한 뒤 Jsoup으로 정화(sanitize)하여 `th:utext`로 내보낸다. 클라이언트 렌더링(marked + DOMPurify, CDN)을 대신 택하지 않은 이유는 비-JS 환경에서도 정상 표시되고 XSS 방어 지점을 서버 한 곳으로 모을 수 있기 때문이다. 댓글(`PostComment`)은 마크다운을 적용하지 않고 평문으로 다룬다.

## Consequences

- `commonmark-java`와 `jsoup`(또는 OWASP Java HTML Sanitizer) 의존이 추가된다.
- DB에는 항상 원본 마크다운이 저장되므로 렌더링 정책을 바꿔도 데이터 마이그레이션이 필요 없다.
- 정화 화이트리스트를 통과하지 못한 태그는 출력에서 제거되므로, 허용 태그 목록이 곧 본문 표현력의 상한이 된다.
