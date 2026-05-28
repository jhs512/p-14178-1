package com.back.domain.post.controller;

import com.back.domain.post.entity.Post;
import com.back.domain.post.service.PostCommentLikeService;
import com.back.domain.post.service.PostCommentService;
import com.back.domain.post.service.PostService;
import com.back.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostCommentController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostCommentLikeService postCommentLikeService;

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable long postId,
                             @AuthenticationPrincipal SecurityUser loginUser,
                             @RequestParam String content,
                             @RequestParam(required = false) Long parentId) {
        Post post = postService.getPost(postId);
        postCommentService.addComment(post, loginUser.getId(), parentId, content);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/edit")
    public String editComment(@PathVariable long id,
                              @AuthenticationPrincipal SecurityUser loginUser,
                              @RequestParam String content) {
        long postId = postCommentService.getComment(id).getPost().getId();
        postCommentService.updateComment(id, loginUser.getId(), content);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable long id,
                                @AuthenticationPrincipal SecurityUser loginUser) {
        long postId = postCommentService.getComment(id).getPost().getId();
        postCommentService.deleteComment(id, loginUser.getId());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/like")
    public String likeComment(@PathVariable long id,
                              @AuthenticationPrincipal SecurityUser loginUser) {
        long postId = postCommentService.getComment(id).getPost().getId();
        postCommentLikeService.toggle(id, loginUser.getId());
        return "redirect:/posts/" + postId;
    }
}
