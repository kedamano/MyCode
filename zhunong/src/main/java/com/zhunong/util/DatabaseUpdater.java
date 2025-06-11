package com.zhunong.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 数据库更新工具类
 * 用于直接执行SQL脚本更新数据库结构
 */
public class DatabaseUpdater {
    
    // 数据库配置信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zhunong?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String SQL_FILE = "update_database.sql";
    
    public static void main(String[] args) {
        System.out.println("==== 数据库更新工具 ====");
        System.out.println("准备执行SQL脚本: " + SQL_FILE);
        System.out.println("数据库URL: " + DB_URL);
        System.out.println("用户名: " + DB_USER);
        
        // 确认操作
        System.out.print("是否继续? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("y")) {
            System.out.println("操作已取消.");
            return;
        }
        
        // 执行SQL脚本
        try {
            executeSqlScript();
            System.out.println("数据库更新成功!");
        } catch (Exception e) {
            System.err.println("数据库更新失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("按任意键退出...");
        scanner.nextLine();
    }
    
    /**
     * 执行SQL脚本文件
     */
    private static void executeSqlScript() throws IOException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // 读取SQL脚本文件
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(SQL_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 忽略注释行
                    if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                        sb.append(line);
                        if (line.trim().endsWith(";")) {
                            sb.append("\n");
                        }
                    }
                }
            }
            
            // 连接数据库
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // 执行SQL语句
            System.out.println("执行SQL脚本...");
            String[] sqlStatements = sb.toString().split(";");
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        System.out.println("执行: " + sql);
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        // 如果列已存在，会抛出异常，可以忽略
                        if (e.getMessage().contains("Duplicate column")) {
                            System.out.println("  - 列已存在，跳过: " + e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }
            
            System.out.println("SQL脚本执行完成.");
        } finally {
            // 关闭资源
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
} 