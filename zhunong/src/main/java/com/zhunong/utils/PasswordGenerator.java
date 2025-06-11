package com.zhunong.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        // 使用BCryptPasswordEncoder生成密码哈希
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        String adminPassword = "admin123";
        String employeePassword = "employee123";
        String farmerPassword = "farmer123";
        
        System.out.println("Admin password hash: " + encoder.encode(adminPassword));
        System.out.println("Employee password hash: " + encoder.encode(employeePassword));
        System.out.println("Farmer password hash: " + encoder.encode(farmerPassword));
        
        // 测试特定哈希是否匹配
        String storedHash = "$2a$10$rPJg/j8zBxGgqF2UVvNKNuKoQJFvNIjJDjYS9t0WT1yTIiPJuZrMm";
        System.out.println("Stored hash matches 'admin123': " + encoder.matches(adminPassword, storedHash));
    }
} 