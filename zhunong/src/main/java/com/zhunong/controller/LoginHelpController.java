package com.zhunong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user/login-help")
public class LoginHelpController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginHelpController.class);
    
    @GetMapping
    public String loginHelp(Model model) {
        logger.info("访问登录帮助页面");
        
        model.addAttribute("adminCredentials", new LoginCredential("admin", "admin123", "系统管理员"));
        model.addAttribute("employeeCredentials", new LoginCredential("employee1", "employee123", "职工"));
        model.addAttribute("farmerCredentials", new LoginCredential("farmer1", "farmer123", "农户"));
        
        return "login-help";
    }
    
    public static class LoginCredential {
        private String username;
        private String password;
        private String role;
        
        public LoginCredential(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public String getRole() {
            return role;
        }
    }
} 