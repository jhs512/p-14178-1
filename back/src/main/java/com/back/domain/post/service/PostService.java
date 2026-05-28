package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.post.dto.PostListItem;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostCommentLikeRepository;
import com.back.domain.post.repository.PostCommentRepository;
import com.back.domain.post.repository.PostLikeRepository;
import com.back.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final MemberRepository memberRepository;

    public Page<PostListItem> getList(Pageable pageable) {
        return postRepository.findAllByOrderByIdDesc(pageable)
                .map(post -> new PostListItem(
                        post.getId(),
                        post.getTitle(),
                        post.getAuthor().getNickname(),
                        post.getCreateDate(),
                        post.getViewCount(),
                        postLikeRepository.countByPost_Id(post.getId()),
                        postCommentRepository.countByPost(post)
                ));
    }

    public Post getPost(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
    }

    @Transactional
    public Post write(long authorId, String title, String content) {
        Member author = memberRepository.getReferenceById(authorId);
        return postRepository.save(new Post(author, title, content));
    }

    @Transactional
    public void update(long postId, long memberId, String title, String content) {
        Post post = getPost(postId);
        if (!post.isAuthor(memberId)) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }
        post.update(title, content);
    }

    @Transactional
    public void delete(long postId, long memberId) {
        Post post = getPost(postId);
        if (!post.isAuthor(memberId)) {
            throw new AccessDeniedException("본인 글만 삭제할 수 있습니다.");
        }

        // FK 순서: 댓글 추천 -> 글 추천 -> 대댓글 -> 최상위 댓글 -> 글
        postCommentLikeRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        postCommentRepository.deleteRepliesByPostId(postId);
        postCommentRepository.deleteRootsByPostId(postId);
        postRepository.delete(post);
    }

    @Transactional
    public void increaseViewCount(long id) {
        postRepository.increaseViewCount(id);
    }
}
