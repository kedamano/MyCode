package com.zhunong.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private String image;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Long farmerId;
    private String farmerName;
    private Integer status; // 0: 待审核, 1: 已上架, 2: 已下架
    private Integer salesCount; // 销量
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 