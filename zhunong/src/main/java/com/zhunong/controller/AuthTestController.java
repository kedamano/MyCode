package com.zhunong.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/auth")
public class AuthTestController {

    @GetMapping
    public String getAuthInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        StringBuilder result = new StringBuilder();
        result.append("Authentication: ").append(auth != null).append("\n");
        
        if (auth != null) {
            result.append("Principal: ").append(auth.getPrincipal()).append("\n");
            result.append("Username: ").append(auth.getName()).append("\n");
            result.append("Authorities: ").append(auth.getAuthorities()).append("\n");
            result.append("Is Authenticated: ").append(auth.isAuthenticated()).append("\n");
        }
        
        return result.toString();
    }
} 