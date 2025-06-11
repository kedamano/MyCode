package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhunong.entity.Account;
import com.zhunong.entity.User;
import com.zhunong.mapper.AccountMapper;
import com.zhunong.mapper.UserMapper;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    @Transactional
    public void register(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置状态
        if ("FARMER".equals(user.getRole())) {
            user.setStatus(0); // 农户需要审核
        } else {
            user.setStatus(1); // 其他用户直接激活
        }
        
        save(user);

        // 创建账户 - 为员工和农户都创建账户
        if ("EMPLOYEE".equals(user.getRole()) || "FARMER".equals(user.getRole())) {
            Account account = new Account();
            account.setUserId(user.getId());
            account.setUsername(user.getUsername());
            account.setBalance(new java.math.BigDecimal("0"));
            accountMapper.insert(account);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        updateById(user);
    }

    @Override
    @Transactional
    public void createEmployee(User user) {
        user.setRole("EMPLOYEE");
        user.setStatus(1);
        register(user);
    }

    @Override
    @Transactional
    public void reviewFarmer(Long id, boolean approved) {
        User farmer = getById(id);
        if (farmer == null || !"FARMER".equals(farmer.getRole())) {
            throw new RuntimeException("农户不存在");
        }
        farmer.setStatus(approved ? 1 : 2);
        updateById(farmer);
        
        // 如果审核通过，确保为农户创建账户
        if (approved) {
            Account existingAccount = accountMapper.selectOne(
                new LambdaQueryWrapper<Account>().eq(Account::getUserId, id)
            );
            
            if (existingAccount == null) {
                Account account = new Account();
                account.setUserId(id);
                account.setUsername(farmer.getUsername());
                account.setBalance(new java.math.BigDecimal("0"));
                accountMapper.insert(account);
            }
        }
    }
    
    @Override
    public long countByRole(String role) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getRole, role)
                .eq(User::getStatus, 1));
    }
    
    @Override
    public List<User> findByRole(String role) {
        return list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, role)
                .eq(User::getStatus, 1));
    }
} 