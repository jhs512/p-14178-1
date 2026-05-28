package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.post.entity.Post;
import com.back.domain.post.entity.PostLike;
import com.back.domain.post.repository.PostLikeRepository;
import com.back.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public long count(long postId) {
        return postLikeRepository.countByPost_Id(postId);
    }

    public boolean likedByMe(long postId, Long memberId) {
        return memberId != null && postLikeRepository.existsByMember_IdAndPost_Id(memberId, postId);
    }

    @Transactional
    public void toggle(long postId, long memberId) {
        postLikeRepository.findByMember_IdAndPost_Id(memberId, postId)
                .ifPresentOrElse(
                        postLikeRepository::delete,
                        () -> {
                            Member member = memberRepository.getReferenceById(memberId);
                            Post post = postRepository.getReferenceById(postId);
                            postLikeRepository.save(new PostLike(member, post));
                        }
                );
    }
}
