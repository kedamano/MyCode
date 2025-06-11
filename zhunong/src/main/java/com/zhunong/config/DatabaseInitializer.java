package com.zhunong.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Initializing database schema...");
        
        try {
            // 检查并添加列（如果需要）
            addMissingColumns();
            
            log.info("Database schema initialization completed successfully");
        } catch (Exception e) {
            log.error("Error initializing database schema", e);
        }
    }
    
    private void addMissingColumns() {
        // 检查并添加Product表的sales_count列
        boolean salesCountExists = checkColumnExists("products", "sales_count");
        if (!salesCountExists) {
            log.info("Adding sales_count column to products table");
            jdbcTemplate.execute("ALTER TABLE products ADD COLUMN sales_count int(11) NOT NULL DEFAULT '0' COMMENT '销量'");
        }
        
        // 检查并添加User表的新列
        Map<String, String> userColumns = new HashMap<>();
        userColumns.put("id_card", "varchar(18) DEFAULT NULL COMMENT '身份证号'");
        userColumns.put("province", "varchar(50) DEFAULT NULL COMMENT '省份'");
        userColumns.put("city", "varchar(50) DEFAULT NULL COMMENT '城市'");
        userColumns.put("district", "varchar(50) DEFAULT NULL COMMENT '区/县'");
        userColumns.put("farm_name", "varchar(100) DEFAULT NULL COMMENT '农场/基地名称'");
        userColumns.put("farming_type", "varchar(50) DEFAULT NULL COMMENT '种植/养殖类型'");
        userColumns.put("description", "text DEFAULT NULL COMMENT '农户简介'");
        
        for (Map.Entry<String, String> entry : userColumns.entrySet()) {
            String columnName = entry.getKey();
            String columnDefinition = entry.getValue();
            
            if (!checkColumnExists("users", columnName)) {
                log.info("Adding {} column to users table", columnName);
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN " + columnName + " " + columnDefinition);
            }
        }
    }
    
    private boolean checkColumnExists(String tableName, String columnName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, columnName);
            boolean exists = count != null && count > 0;
            if (exists) {
                log.debug("Column {} already exists in table {}", columnName, tableName);
            }
            return exists;
        } catch (Exception e) {
            log.error("Error checking if column exists", e);
            return false;
        }
    }
} 