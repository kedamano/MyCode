package com.zhunong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhunong.entity.Account;

import java.math.BigDecimal;

public interface AccountService extends IService<Account> {
    // 创建账户
    Account createAccount(Long userId);
    
    // 获取用户账户
    Account getByUserId(Long userId);
    
    // 充值
    void recharge(Long userId, BigDecimal amount);
    
    // 消费
    void consume(Long userId, BigDecimal amount);
    
    // 获取余额
    BigDecimal getBalance(Long userId);
} 