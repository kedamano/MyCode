package com.zhunong.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String address;
    private String role; // ADMIN, EMPLOYEE, FARMER
    private Integer status; // 0: 待审核, 1: 正常, 2: 禁用
    
    // 扩展信息
    private String idCard;    // 身份证号
    private String province;  // 省份
    private String city;      // 城市
    private String district;  // 区/县
    private String farmName;  // 农场/基地名称
    private String farmingType; // 种植/养殖类型
    private String description; // 农户简介
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private Account account;
}