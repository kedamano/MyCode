package com.zhunong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/password")
public class PasswordTestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }

    @GetMapping("/match")
    public String matchPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        return "Raw password: " + rawPassword + 
               "\nEncoded password: " + encodedPassword + 
               "\nMatches: " + matches;
    }
    
    @GetMapping("/test-admin")
    public String testAdminPassword() {
        String rawPassword = "admin123";
        String storedHash = "$2a$10$rPJg/j8zBxGgqF2UVvNKNuKoQJFvNIjJDjYS9t0WT1yTIiPJuZrMm";
        
        // 使用默认的BCryptPasswordEncoder测试
        boolean matchesWithDefault = new BCryptPasswordEncoder().matches(rawPassword, storedHash);
        
        // 使用当前应用配置的PasswordEncoder测试
        boolean matchesWithConfigured = passwordEncoder.matches(rawPassword, storedHash);
        
        // 生成一个新的哈希值进行比较
        String newHash = passwordEncoder.encode(rawPassword);
        
        return "Raw password: " + rawPassword + 
               "\nStored hash: " + storedHash + 
               "\nMatches with default encoder: " + matchesWithDefault + 
               "\nMatches with configured encoder: " + matchesWithConfigured + 
               "\nNewly generated hash: " + newHash;
    }
} 