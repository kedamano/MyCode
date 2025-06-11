package com.zhunong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhunong.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    // 根据用户名查询用户
    User getByUsername(String username);
    
    // 注册用户
    void register(User user);
    
    // 更新用户状态
    void updateStatus(Long userId, Integer status);
    
    // 创建职工账户
    void createEmployee(User user);
    
    // 审核农户注册
    void reviewFarmer(Long id, boolean approved);
    
    // 按角色统计用户数量
    long countByRole(String role);
    
    // 按角色查找用户
    List<User> findByRole(String role);
} 