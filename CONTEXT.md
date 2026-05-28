# 게시판 (Board)

회원이 글과 댓글을 작성하고, 서로의 글·댓글을 추천하며, 글 조회수가 집계되는 커뮤니티 게시판 컨텍스트. 루트 패키지는 `com.back`.

## Language

**Member** (회원):
시스템에 가입한 사람. `username`(로그인 ID), `password`, `nickname`을 가진다. `username`과 `nickname`은 모두 유일하며 `username`은 가입 후 변경 불가, `nickname`·`password`는 "내 정보"에서 수정 가능. 탈퇴 기능은 없다. 관리자 역할 없이 전원 동등.
_Avoid_: User, Account, 유저

**Post** (글):
**Member**가 작성한 게시글. `title`(평문)과 `content`를 가진다. `content`는 **마크다운 원본**으로 저장하고, 출력 시 서버에서 HTML로 변환·정화한다(아래 "마크다운" 참고).
_Avoid_: Article, Board, 게시물

**마크다운 (Markdown)**:
**Post** `content`의 작성 형식. **PostComment**는 마크다운이 아닌 평문. 저장은 원본 마크다운, 렌더는 서버에서 commonmark로 HTML 변환 후 Jsoup으로 정화하여 출력(XSS 차단). docs/adr/0001 참고.

**PostComment** (댓글):
특정 **Post**에 달린 **Member**의 댓글. 다른 PostComment를 부모로 가질 수 있다(1단계 대댓글). 부모는 반드시 최상위 댓글이어야 한다(대댓글에 답글 불가).
_Avoid_: Comment, Reply, 답글

**대댓글 (reply)**:
다른 **PostComment**(반드시 최상위)를 부모로 가진 **PostComment**. 별도 엔티티가 아니라 parent FK로 구분.

**삭제된 댓글**:
대댓글이 달린 부모 댓글을 삭제하면 행을 지우지 않고 내용을 "삭제된 댓글입니다"로 가린 상태(`isDeleted`=true)로 둔다. 대댓글은 그대로 보존. 대댓글이 없으면 일반(하드) 삭제.

**PostLike** (글 추천):
**Member**가 **Post**에 표시한 추천. 한 Member는 한 Post에 1회만 추천(중복 불가), 다시 누르면 취소(토글). 본인 글도 추천 가능. "추천수"는 한 Post에 달린 PostLike 개수.
_Avoid_: Vote, Recommendation 엔티티명

**PostCommentLike** (댓글 추천):
**Member**가 **PostComment**에 표시한 추천. PostLike와 동일 규칙(1회 제한·토글·본인 허용). "추천수"는 한 PostComment에 달린 PostCommentLike 개수.
_Avoid_: CommentLike(전체명 생략 금지)

**추천 (Like)**:
도메인 한국어 용어 "추천"은 코드에서 `Like`로 표기한다(PostLike, PostCommentLike). UI 라벨은 "추천수".

**viewCount** (조회수):
한 **Post**가 조회된 누적 횟수. 본 글 ID를 쿠키에 누적해 매일 자정까지 같은 글 재조회는 증가시키지 않는다(비로그인 포함). 증가는 별도 UPDATE로 처리해 `modifyDate`를 건드리지 않는다.
