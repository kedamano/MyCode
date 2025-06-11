package com.zhunong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Bean
    CommandLineRunner init() {
        return args -> {
            // 初始化商品图片存储目录
            String uploadDir = "src/main/resources/static/images/products";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("创建商品图片存储目录: " + uploadPath.toAbsolutePath());
            } else {
                System.out.println("商品图片存储目录已存在: " + uploadPath.toAbsolutePath());
            }
        };
    }
} 