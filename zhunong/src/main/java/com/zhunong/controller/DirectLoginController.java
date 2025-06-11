package com.zhunong.controller;

import com.zhunong.entity.User;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@RequestMapping("/direct-login")
public class DirectLoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    public String directLoginPage() {
        return "direct-login";
    }
    
    @PostMapping
    public String directLogin(@RequestParam String username, @RequestParam String password) {
        User user = userService.getByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword()) || user.getStatus() != 1) {
            return "redirect:/user/login?error=true";
        }
        
        // 创建认证令牌，注意授权需要添加ROLE_前缀
        Authentication auth = new UsernamePasswordAuthenticationToken(
            username,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        
        // 设置认证
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // 根据角色重定向到指定页面
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin";
        } else if ("EMPLOYEE".equals(user.getRole())) {
            return "redirect:/product/list";
        } else if ("FARMER".equals(user.getRole())) {
            return "redirect:/product/farmer/list";
        } else {
            return "redirect:/";
        }
    }
} 