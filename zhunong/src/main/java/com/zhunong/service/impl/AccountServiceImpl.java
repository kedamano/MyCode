package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhunong.entity.Account;
import com.zhunong.entity.User;
import com.zhunong.mapper.AccountMapper;
import com.zhunong.service.AccountService;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Account createAccount(Long userId) {
        // 检查是否已存在账户
        Account existingAccount = getByUserId(userId);
        if (existingAccount != null) {
            return existingAccount;
        }
        
        // 获取用户信息以获取username
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，无法创建账户");
        }
        
        // 创建新账户
        Account account = new Account();
        account.setUserId(userId);
        account.setUsername(user.getUsername()); // 设置username字段
        account.setBalance(BigDecimal.ZERO);
        save(account);
        
        return account;
    }

    @Override
    public Account getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId));
    }

    @Override
    @Transactional
    public void recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        
        Account account = getByUserId(userId);
        if (account == null) {
            account = createAccount(userId);
        }
        
        account.setBalance(account.getBalance().add(amount));
        updateById(account);
    }

    @Override
    @Transactional
    public void consume(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("消费金额必须大于0");
        }
        
        Account account = getByUserId(userId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("账户余额不足");
        }
        
        account.setBalance(account.getBalance().subtract(amount));
        updateById(account);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        Account account = getByUserId(userId);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }
} 