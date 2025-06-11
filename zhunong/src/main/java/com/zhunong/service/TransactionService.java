package com.zhunong.service;

import com.zhunong.entity.Order;

import java.math.BigDecimal;

public interface TransactionService {
    /**
     * 处理订单支付
     * 职工账户余额减少，农户账户余额增加
     * 
     * @param order 订单信息
     * @param employeeId 职工ID
     * @param farmerId 农户ID
     * @param amount 支付金额
     * @return 是否支付成功
     */
    boolean processOrderPayment(Order order, Long employeeId, Long farmerId, BigDecimal amount);
    
    /**
     * 退款处理
     * 将金额从农户账户返还到职工账户
     * 
     * @param order 订单信息
     * @param employeeId 职工ID
     * @param farmerId 农户ID
     * @param amount 退款金额
     * @return 是否退款成功
     */
    boolean processRefund(Order order, Long employeeId, Long farmerId, BigDecimal amount);
} 