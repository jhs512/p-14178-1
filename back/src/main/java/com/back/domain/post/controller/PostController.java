package com.back.domain.post.controller;

import com.back.domain.post.dto.PostForm;
import com.back.domain.post.entity.Post;
import com.back.domain.post.service.PostCommentService;
import com.back.domain.post.service.PostLikeService;
import com.back.domain.post.service.PostService;
import com.back.global.markdown.MarkdownRenderer;
import com.back.global.security.SecurityUser;
import com.back.global.viewcount.ViewCountCookiePolicy;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostCommentService postCommentService;
    private final MarkdownRenderer markdownRenderer;
    private final ViewCountCookiePolicy viewCountCookiePolicy;

    @GetMapping("/posts")
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("postPage", postService.getList(pageable));
        return "post/list";
    }

    @GetMapping("/posts/write")
    public String writeForm(@ModelAttribute("form") PostForm form) {
        return "post/write";
    }

    @PostMapping("/posts")
    public String write(@AuthenticationPrincipal SecurityUser loginUser,
                        @Valid @ModelAttribute("form") PostForm form,
                        org.springframework.validation.BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "post/write";
        }

        Post post = postService.write(loginUser.getId(), form.getTitle(), form.getContent());
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable long id,
                         @AuthenticationPrincipal SecurityUser loginUser,
                         @CookieValue(value = ViewCountCookiePolicy.COOKIE_NAME, required = false) String viewedCookie,
                         HttpServletResponse response,
                         Model model) {
        ViewCountCookiePolicy.Decision decision = viewCountCookiePolicy.decide(viewedCookie, id);
        if (decision.shouldIncrement()) {
            postService.increaseViewCount(id);
        }
        writeViewedCookie(response, decision);

        Post post = postService.getPost(id);
        Long memberId = loginUser != null ? loginUser.getId() : null;

        model.addAttribute("post", post);
        model.addAttribute("contentHtml", markdownRenderer.render(post.getContent()));
        model.addAttribute("postLikeCount", postLikeService.count(id));
        model.addAttribute("postLikedByMe", postLikeService.likedByMe(id, memberId));
        model.addAttribute("comments", postCommentService.getCommentsForPost(post, memberId));
        model.addAttribute("canEdit", memberId != null && post.isAuthor(memberId));
        return "post/detail";
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable long id,
                           @AuthenticationPrincipal SecurityUser loginUser,
                           @ModelAttribute("form") PostForm form,
                           Model model) {
        Post post = postService.getPost(id);
        if (!post.isAuthor(loginUser.getId())) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        model.addAttribute("postId", id);
        return "post/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String edit(@PathVariable long id,
                       @AuthenticationPrincipal SecurityUser loginUser,
                       @Valid @ModelAttribute("form") PostForm form,
                       org.springframework.validation.BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "post/edit";
        }
        postService.update(id, loginUser.getId(), form.getTitle(), form.getContent());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable long id,
                         @AuthenticationPrincipal SecurityUser loginUser) {
        postService.delete(id, loginUser.getId());
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/like")
    public String like(@PathVariable long id,
                       @AuthenticationPrincipal SecurityUser loginUser) {
        postLikeService.toggle(id, loginUser.getId());
        return "redirect:/posts/" + id;
    }

    private void writeViewedCookie(HttpServletResponse response, ViewCountCookiePolicy.Decision decision) {
        Cookie cookie = new Cookie(ViewCountCookiePolicy.COOKIE_NAME, decision.cookieValue());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(decision.maxAgeSeconds());
        response.addCookie(cookie);
    }
}
