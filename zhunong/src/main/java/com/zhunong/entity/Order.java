package com.zhunong.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    private Long employeeId;
    private String employeeName;
    private String employeePhone;
    private String employeeDepartment;
    private BigDecimal totalAmount;
    private String address;
    private String phone;
    private String remark;
    private String status; // 待付款, 已完成, 已取消
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    
    @TableField(exist = false)
    private List<OrderItem> items;
} 