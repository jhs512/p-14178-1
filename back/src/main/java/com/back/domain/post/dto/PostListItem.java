package com.back.domain.post.dto;

import java.time.LocalDateTime;

public record PostListItem(
        long id,
        String title,
        String authorNickname,
        LocalDateTime createDate,
        long viewCount,
        long likeCount,
        long commentCount
) {
}
