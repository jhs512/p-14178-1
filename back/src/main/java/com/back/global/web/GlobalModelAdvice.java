package com.back.global.web;

import com.back.global.security.SecurityUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("loginUser")
    public SecurityUser loginUser(@AuthenticationPrincipal SecurityUser loginUser) {
        return loginUser;
    }
}
