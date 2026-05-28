package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.post.entity.PostComment;
import com.back.domain.post.entity.PostCommentLike;
import com.back.domain.post.repository.PostCommentLikeRepository;
import com.back.domain.post.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentLikeService {

    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;

    public long count(long commentId) {
        return postCommentLikeRepository.countByPostComment_Id(commentId);
    }

    public boolean likedByMe(long commentId, Long memberId) {
        return memberId != null && postCommentLikeRepository.existsByMember_IdAndPostComment_Id(memberId, commentId);
    }

    @Transactional
    public void toggle(long commentId, long memberId) {
        postCommentLikeRepository.findByMember_IdAndPostComment_Id(memberId, commentId)
                .ifPresentOrElse(
                        postCommentLikeRepository::delete,
                        () -> {
                            Member member = memberRepository.getReferenceById(memberId);
                            PostComment comment = postCommentRepository.getReferenceById(commentId);
                            postCommentLikeRepository.save(new PostCommentLike(member, comment));
                        }
                );
    }
}
