package com.zhunong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.zhunong.mapper")
public class ZhunongApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhunongApplication.class, args);
    }
} 