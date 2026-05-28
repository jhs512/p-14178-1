package com.back.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentDto(
        long id,
        String authorNickname,
        String content,
        boolean deleted,
        boolean reply,
        boolean mine,
        LocalDateTime createDate,
        long likeCount,
        boolean likedByMe,
        List<CommentDto> replies
) {
    public String displayContent() {
        return deleted ? "삭제된 댓글입니다." : content;
    }
}
