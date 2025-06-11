package com.zhunong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/fix-login")
public class FixLoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/fix-passwords")
    public String fixPasswords() {
        try {
            // 更新职工密码为employee123
            String employeePassword = "employee123";
            String employeeHash = passwordEncoder.encode(employeePassword);
            jdbcTemplate.update("UPDATE users SET password = ? WHERE role = 'EMPLOYEE'", employeeHash);
            
            // 更新农户密码为farmer123
            String farmerPassword = "farmer123";
            String farmerHash = passwordEncoder.encode(farmerPassword);
            jdbcTemplate.update("UPDATE users SET password = ? WHERE role = 'FARMER'", farmerHash);
            
            // 确保所有用户状态正常
            jdbcTemplate.update("UPDATE users SET status = 1 WHERE role IN ('EMPLOYEE', 'FARMER')");
            
            return "密码已重置，职工密码: employee123, 农户密码: farmer123";
        } catch (Exception e) {
            return "密码重置失败: " + e.getMessage();
        }
    }
    
    @GetMapping("/md5-passwords")
    public String setMd5Passwords() {
        try {
            // 使用MD5哈希值直接更新密码
            // employee123 的MD5哈希值
            jdbcTemplate.update("UPDATE users SET password = '033836b6cedd9a857d82681aafadbc19' WHERE role = 'EMPLOYEE'");
            
            // farmer123 的MD5哈希值
            jdbcTemplate.update("UPDATE users SET password = '4b3bcc3fd4c3c0ac234af3b9fd81c899' WHERE role = 'FARMER'");
            
            // 确保所有用户状态正常
            jdbcTemplate.update("UPDATE users SET status = 1 WHERE role IN ('EMPLOYEE', 'FARMER')");
            
            return "密码已使用MD5哈希重置，职工密码: employee123, 农户密码: farmer123";
        } catch (Exception e) {
            return "密码重置失败: " + e.getMessage();
        }
    }
} 