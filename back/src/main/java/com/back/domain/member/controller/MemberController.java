package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberJoinForm;
import com.back.domain.member.dto.MemberProfileForm;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/join")
    public String joinForm(@ModelAttribute("form") MemberJoinForm form) {
        return "member/join";
    }

    @PostMapping("/member/join")
    public String join(@Valid @ModelAttribute("form") MemberJoinForm form,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        try {
            memberService.join(form.getUsername(), form.getPassword(), form.getNickname());
        } catch (IllegalStateException e) {
            bindingResult.reject("joinFailed", e.getMessage());
            return "member/join";
        }

        return "redirect:/member/login";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }

    @GetMapping("/member/me")
    public String me(@AuthenticationPrincipal SecurityUser loginUser,
                     @ModelAttribute("form") MemberProfileForm form,
                     Model model) {
        Member member = memberService.getById(loginUser.getId());
        form.setNickname(member.getNickname());
        model.addAttribute("member", member);
        return "member/me";
    }

    @PostMapping("/member/me")
    public String updateMe(@AuthenticationPrincipal SecurityUser loginUser,
                           @Valid @ModelAttribute("form") MemberProfileForm form,
                           BindingResult bindingResult,
                           Model model) {
        Member member = memberService.getById(loginUser.getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return "member/me";
        }

        try {
            memberService.updateProfile(loginUser.getId(), form.getNickname(), form.getPassword());
        } catch (IllegalStateException e) {
            bindingResult.reject("updateFailed", e.getMessage());
            model.addAttribute("member", member);
            return "member/me";
        }

        return "redirect:/member/me";
    }
}
