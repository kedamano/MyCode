package com.zhunong.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 直接执行数据库更新的工具类
 * 不需要SQL文件，直接执行ALTER TABLE语句
 */
public class DirectDatabaseUpdater {
    // 数据库配置信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zhunong?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    public static void main(String[] args) {
        System.out.println("==== 数据库直接更新工具 ====");
        System.out.println("此工具将直接添加缺失的列到数据库中");
        System.out.println("数据库URL: " + DB_URL);
        System.out.println("用户名: " + DB_USER);
        
        try {
            executeUpdates();
            System.out.println("数据库更新成功!");
        } catch (Exception e) {
            System.err.println("数据库更新失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 等待用户按键
        System.out.println("按 Enter 键退出...");
        try {
            System.in.read();
        } catch (Exception e) {
            // 忽略
        }
    }
    
    /**
     * 执行数据库更新
     */
    private static void executeUpdates() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // 连接数据库
            System.out.println("\n连接数据库...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // 添加用户表的新列
            addColumn(stmt, "users", "id_card", "varchar(18) DEFAULT NULL COMMENT '身份证号'");
            addColumn(stmt, "users", "province", "varchar(50) DEFAULT NULL COMMENT '省份'");
            addColumn(stmt, "users", "city", "varchar(50) DEFAULT NULL COMMENT '城市'");
            addColumn(stmt, "users", "district", "varchar(50) DEFAULT NULL COMMENT '区/县'");
            addColumn(stmt, "users", "farm_name", "varchar(100) DEFAULT NULL COMMENT '农场/基地名称'");
            addColumn(stmt, "users", "farming_type", "varchar(50) DEFAULT NULL COMMENT '种植/养殖类型'");
            addColumn(stmt, "users", "description", "text DEFAULT NULL COMMENT '农户简介'");
            
            // 添加商品表的新列
            addColumn(stmt, "products", "sales_count", "int(11) NOT NULL DEFAULT '0' COMMENT '销量'");
            
            System.out.println("\n数据库更新完成!");
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
    
    /**
     * 添加列，如果列已存在则忽略错误
     */
    private static void addColumn(Statement stmt, String table, String column, String definition) {
        try {
            // 检查列是否存在
            String checkSql = String.format(
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = '%s' AND column_name = '%s'",
                table, column
            );
            
            boolean exists = false;
            ResultSet rs = stmt.executeQuery(checkSql);
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
            rs.close();
            
            if (exists) {
                System.out.println("列已存在，跳过: " + table + "." + column);
            } else {
                // 添加列
                String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s", table, column, definition);
                System.out.println("执行: " + alterSql);
                stmt.executeUpdate(alterSql);
                System.out.println("  - 添加成功: " + table + "." + column);
            }
        } catch (SQLException e) {
            // 如果列已存在，MySQL会抛出错误，这里捕获并忽略
            if (e.getMessage().contains("Duplicate column") || e.getMessage().contains("already exists")) {
                System.out.println("列已存在(从错误中检测): " + table + "." + column);
            } else {
                System.err.println("添加列失败: " + table + "." + column + " - " + e.getMessage());
            }
        }
    }
} 