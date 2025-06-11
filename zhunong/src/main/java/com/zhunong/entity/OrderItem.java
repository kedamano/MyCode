package com.zhunong.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private Long farmerId;
    private String farmerName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 