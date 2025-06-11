package com.zhunong.utils;

import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * MD5密码生成工具类
 */
public class MD5PasswordGenerator {
    
    public static void main(String[] args) {
        String adminPassword = "admin123";
        String employeePassword = "employee123";
        String farmerPassword = "farmer123";
        
        System.out.println("Admin password (admin123) hash (MD5): " + md5(adminPassword));
        System.out.println("Employee password (employee123) hash (MD5): " + md5(employeePassword));
        System.out.println("Farmer password (farmer123) hash (MD5): " + md5(farmerPassword));
        
        // 输出FixLoginController中使用的哈希值
        System.out.println("\nFixLoginController中使用的哈希值:");
        System.out.println("Employee: 6e9a0651d7e08ca7d1fc6b8a20a744a9");
        System.out.println("Farmer: 4c56ff4ce4aaf9573aa5dff913df997a");
    }
    
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            BigInteger no = new BigInteger(1, digest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 