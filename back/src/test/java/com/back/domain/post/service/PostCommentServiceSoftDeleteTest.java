package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.entity.Post;
import com.back.domain.post.entity.PostComment;
import com.back.domain.post.repository.PostCommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostCommentServiceSoftDeleteTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PostService postService;
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private PostCommentRepository postCommentRepository;

    @Test
    void 대댓글이_없는_댓글은_하드_삭제된다() {
        Member member = memberService.join("hard1", "1234", "하드유저1");
        Post post = postService.write(member.getId(), "제목", "내용");
        PostComment comment = postCommentService.addComment(post, member.getId(), null, "댓글");

        postCommentService.deleteComment(comment.getId(), member.getId());

        assertThat(postCommentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void 대댓글이_있는_댓글은_소프트_삭제되고_대댓글은_보존된다() {
        Member author = memberService.join("soft1", "1234", "소프트유저1");
        Member replier = memberService.join("soft2", "1234", "소프트유저2");
        Post post = postService.write(author.getId(), "제목", "내용");
        PostComment root = postCommentService.addComment(post, author.getId(), null, "부모 댓글");
        postCommentService.addComment(post, replier.getId(), root.getId(), "대댓글");

        postCommentService.deleteComment(root.getId(), author.getId());

        PostComment reloaded = postCommentRepository.findById(root.getId()).orElseThrow();
        assertThat(reloaded.isDeleted()).isTrue();
        assertThat(postCommentRepository.countByParent(reloaded)).isEqualTo(1);
    }
}
