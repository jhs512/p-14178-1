package com.back.global.initData;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.entity.Post;
import com.back.domain.post.entity.PostComment;
import com.back.domain.post.service.PostCommentService;
import com.back.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberService memberService;
    private final PostService postService;
    private final PostCommentService postCommentService;

    // 자기 자신을 프록시로 주입해 @Transactional이 적용되도록 한다(자기호출 회피).
    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> self.work();
    }

    @Transactional
    public void work() {
        // 회원이 한 명이라도 있으면 샘플 데이터 생성을 중단한다.
        if (memberService.count() > 0) {
            return;
        }

        Member user1 = memberService.join("user1", "1234", "유저1");
        Member user2 = memberService.join("user2", "1234", "유저2");
        Member user3 = memberService.join("user3", "1234", "유저3");
        Member user4 = memberService.join("user4", "1234", "유저4");
        Member user5 = memberService.join("user5", "1234", "유저5");

        Post post1 = postService.write(user1.getId(), "첫 번째 글",
                "# 환영합니다\n\n이 글은 **마크다운**으로 작성되었습니다.\n\n- 첫째\n- 둘째");
        Post post2 = postService.write(user2.getId(), "두 번째 글", "두 번째 글의 _내용_ 입니다.");
        postService.write(user1.getId(), "세 번째 글", "세 번째 글입니다.\n\n```\ncode block\n```");
        postService.write(user3.getId(), "네 번째 글", "네 번째 글입니다.");
        postService.write(user4.getId(), "다섯 번째 글", "다섯 번째 글입니다.");

        // 댓글 5개 (대댓글 2개 포함)
        PostComment c1 = postCommentService.addComment(post1, user2.getId(), null, "첫 글 잘 봤습니다!");
        postCommentService.addComment(post1, user3.getId(), c1.getId(), "저도 동의합니다.");
        PostComment c4 = postCommentService.addComment(post1, user4.getId(), null, "유익하네요.");
        postCommentService.addComment(post1, user5.getId(), c4.getId(), "맞아요!");
        postCommentService.addComment(post2, user1.getId(), null, "두 번째 글 댓글입니다.");
    }
}
