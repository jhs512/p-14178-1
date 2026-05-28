package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.post.dto.CommentDto;
import com.back.domain.post.entity.Post;
import com.back.domain.post.entity.PostComment;
import com.back.domain.post.repository.PostCommentLikeRepository;
import com.back.domain.post.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final MemberRepository memberRepository;

    public PostComment getComment(long id) {
        return postCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    @Transactional
    public PostComment addComment(Post post, long authorId, Long parentId, String content) {
        PostComment parent = null;
        if (parentId != null) {
            parent = getComment(parentId);
            if (!parent.getPost().getId().equals(post.getId())) {
                throw new IllegalArgumentException("부모 댓글이 이 글의 댓글이 아닙니다.");
            }
            if (parent.isReply()) {
                throw new IllegalStateException("대댓글에는 답글을 달 수 없습니다.");
            }
        }

        Member author = memberRepository.getReferenceById(authorId);
        return postCommentRepository.save(new PostComment(post, author, parent, content));
    }

    @Transactional
    public void updateComment(long commentId, long memberId, String content) {
        PostComment comment = getComment(commentId);
        if (!comment.isAuthor(memberId)) {
            throw new AccessDeniedException("본인 댓글만 수정할 수 있습니다.");
        }
        comment.update(content);
    }

    @Transactional
    public void deleteComment(long commentId, long memberId) {
        PostComment comment = getComment(commentId);
        if (!comment.isAuthor(memberId)) {
            throw new AccessDeniedException("본인 댓글만 삭제할 수 있습니다.");
        }

        boolean rootWithReplies = !comment.isReply() && postCommentRepository.countByParent(comment) > 0;
        if (rootWithReplies) {
            // 대댓글이 달린 부모 댓글: 소프트 삭제(대댓글 보존)
            comment.markDeleted();
        } else {
            // 하드 삭제: 추천 먼저 제거
            postCommentLikeRepository.deleteByPostCommentId(commentId);
            postCommentRepository.delete(comment);
        }
    }

    public List<CommentDto> getCommentsForPost(Post post, Long currentMemberId) {
        List<PostComment> comments = postCommentRepository.findByPostOrderByIdAsc(post);

        Map<Long, List<CommentDto>> repliesByParent = new LinkedHashMap<>();
        List<PostComment> roots = new ArrayList<>();

        for (PostComment c : comments) {
            if (c.isReply()) {
                repliesByParent
                        .computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>())
                        .add(toDto(c, currentMemberId, List.of()));
            } else {
                roots.add(c);
            }
        }

        List<CommentDto> result = new ArrayList<>();
        for (PostComment root : roots) {
            List<CommentDto> replies = repliesByParent.getOrDefault(root.getId(), List.of());
            result.add(toDto(root, currentMemberId, replies));
        }
        return result;
    }

    private CommentDto toDto(PostComment c, Long currentMemberId, List<CommentDto> replies) {
        boolean mine = !c.isDeleted()
                && currentMemberId != null
                && c.isAuthor(currentMemberId);

        return new CommentDto(
                c.getId(),
                c.getAuthor().getNickname(),
                c.getContent(),
                c.isDeleted(),
                c.isReply(),
                mine,
                c.getCreateDate(),
                postCommentLikeRepository.countByPostComment_Id(c.getId()),
                currentMemberId != null && postCommentLikeRepository.existsByMember_IdAndPostComment_Id(currentMemberId, c.getId()),
                replies
        );
    }
}
