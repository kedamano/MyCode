package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhunong.entity.Account;
import com.zhunong.entity.Order;
import com.zhunong.entity.User;
import com.zhunong.service.AccountService;
import com.zhunong.service.TransactionService;
import com.zhunong.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean processOrderPayment(Order order, Long employeeId, Long farmerId, BigDecimal amount) {
        try {
            log.debug("处理订单支付: 职工ID={}, 产品中的农户ID={}, 金额={}", employeeId, farmerId, amount);
            
            // 通过姓名查找真实农户用户
            User farmer = userService.list(new LambdaQueryWrapper<User>()
                    .eq(User::getRole, "FARMER")
                    .eq(User::getStatus, 1)
                    .like(User::getRealName, order.getItems().get(0).getFarmerName()))
                    .stream()
                    .findFirst()
                    .orElse(null);
            
            if (farmer == null) {
                log.error("找不到名为{}的农户用户", order.getItems().get(0).getFarmerName());
                throw new RuntimeException("找不到对应的农户用户");
            }
            
            Long realFarmerId = farmer.getId();
            log.debug("找到对应的农户用户: ID={}, 用户名={}, 真实姓名={}", 
                    realFarmerId, farmer.getUsername(), farmer.getRealName());
            
            // 1. 从职工账户扣款
            accountService.consume(employeeId, amount);
            log.debug("已从职工账户扣款: {}", amount);
            
            // 2. 农户账户增加余额
            accountService.recharge(realFarmerId, amount);
            log.debug("已向农户账户(ID={})增加余额: {}", realFarmerId, amount);
            
            // 3. 记录交易日志，可以在这里添加交易记录保存逻辑
            
            return true;
        } catch (Exception e) {
            // 如果出现异常，事务会自动回滚
            log.error("订单支付处理失败", e);
            throw new RuntimeException("订单支付处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean processRefund(Order order, Long employeeId, Long farmerId, BigDecimal amount) {
        try {
            log.debug("处理订单退款: 职工ID={}, 农户ID={}, 金额={}", employeeId, farmerId, amount);
            
            // 通过姓名查找真实农户用户
            User farmer = userService.list(new LambdaQueryWrapper<User>()
                    .eq(User::getRole, "FARMER")
                    .eq(User::getStatus, 1)
                    .like(User::getRealName, order.getItems().get(0).getFarmerName()))
                    .stream()
                    .findFirst()
                    .orElse(null);
            
            if (farmer == null) {
                log.error("找不到名为{}的农户用户", order.getItems().get(0).getFarmerName());
                throw new RuntimeException("找不到对应的农户用户");
            }
            
            Long realFarmerId = farmer.getId();
            log.debug("找到对应的农户用户: ID={}, 用户名={}, 真实姓名={}", 
                    realFarmerId, farmer.getUsername(), farmer.getRealName());
            
            // 1. 检查农户账户余额是否足够
            Account farmerAccount = accountService.getByUserId(realFarmerId);
            if (farmerAccount == null || farmerAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("农户账户余额不足，无法退款");
            }
            
            // 2. 从农户账户扣款
            accountService.consume(realFarmerId, amount);
            log.debug("已从农户账户扣款: {}", amount);
            
            // 3. 职工账户增加余额
            accountService.recharge(employeeId, amount);
            log.debug("已向职工账户增加余额: {}", amount);
            
            // 4. 记录交易日志，可以在这里添加交易记录保存逻辑
            
            return true;
        } catch (Exception e) {
            // 如果出现异常，事务会自动回滚
            log.error("订单退款处理失败", e);
            throw new RuntimeException("订单退款处理失败: " + e.getMessage(), e);
        }
    }
} 