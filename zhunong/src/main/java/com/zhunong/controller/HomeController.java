package com.zhunong.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        } else if (role.equals("ROLE_EMPLOYEE")) {
            return "redirect:/product/list";
        } else if (role.equals("ROLE_FARMER")) {
            return "redirect:/farmer/home";
        } else {
            return "redirect:/user/login";
        }
    }
} 