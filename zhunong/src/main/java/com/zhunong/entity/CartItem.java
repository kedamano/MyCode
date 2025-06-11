package com.zhunong.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart_items")
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("employee_id")
    private Long employeeId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("product_image")
    private String productImage;
    
    @TableField("farmer_id")
    private Long farmerId;
    
    @TableField("farmer_name")
    private String farmerName;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("subtotal")
    private BigDecimal subtotal;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
} 