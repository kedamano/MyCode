package com.zhunong.controller;

import com.zhunong.entity.User;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-login")
public class LoginTestController {

    private static final Logger logger = LoggerFactory.getLogger(LoginTestController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    public String testLogin(@RequestParam String username, @RequestParam String password) {
        User user = userService.getByUsername(username);
        if (user == null) {
            return "用户不存在";
        }
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        logger.info("测试登录: 用户={}, 密码={}, 匹配结果={}", username, password, matches);
        
        return "用户: " + username + 
               "\n密码匹配: " + matches + 
               "\n角色: " + user.getRole() + 
               "\n状态: " + user.getStatus() + 
               "\n存储的密码哈希: " + user.getPassword();
    }
    
    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        logger.info("密码编码: 原始密码={}, 编码后={}", password, encoded);
        return "原始密码: " + password + "\n编码后: " + encoded;
    }
    
    @GetMapping("/match")
    public String matchPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("密码匹配测试: 原始密码={}, 编码密码={}, 匹配结果={}", rawPassword, encodedPassword, matches);
        return "原始密码: " + rawPassword + "\n编码密码: " + encodedPassword + "\n匹配结果: " + matches;
    }
    
    @GetMapping("/users")
    public Map<String, Object> listTestUsers() {
        Map<String, Object> result = new HashMap<>();
        
        // 管理员账号
        Map<String, String> admin = new HashMap<>();
        admin.put("username", "admin");
        admin.put("password", "admin123");
        admin.put("role", "ADMIN");
        
        // 职工账号
        Map<String, String> employee = new HashMap<>();
        employee.put("username", "employee1");
        employee.put("password", "employee123");
        employee.put("role", "EMPLOYEE");
        
        // 农户账号
        Map<String, String> farmer = new HashMap<>();
        farmer.put("username", "farmer1");
        farmer.put("password", "farmer123");
        farmer.put("role", "FARMER");
        
        result.put("admin", admin);
        result.put("employee", employee);
        result.put("farmer", farmer);
        
        return result;
    }
} 