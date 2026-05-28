package com.back.domain.post.entity;

import com.back.domain.member.entity.Member;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    // 최상위 댓글이면 null, 대댓글이면 부모(최상위) 댓글.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    public PostComment(Post post, Member author, PostComment parent, String content) {
        this.post = post;
        this.author = author;
        this.parent = parent;
        this.content = content;
    }

    public boolean isReply() {
        return parent != null;
    }

    public void update(String content) {
        this.content = content;
    }

    public void markDeleted() {
        this.deleted = true;
    }

    public boolean isAuthor(long memberId) {
        return author.getId() == memberId;
    }
}
