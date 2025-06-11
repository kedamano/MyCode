package com.zhunong.security;

import com.zhunong.entity.User;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("尝试加载用户: {}", username);
        
        User user = userService.getByUsername(username);
        if (user == null) {
            logger.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        
        logger.debug("找到用户: {}, 角色: {}, 状态: {}", username, user.getRole(), user.getStatus());
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            logger.warn("用户状态异常: {}, 状态码: {}", username, user.getStatus());
            
            if (user.getStatus() == 0) {
                throw new RuntimeException("用户待审核，请联系管理员");
            } else if (user.getStatus() == 2) {
                throw new RuntimeException("用户已禁用，请联系管理员");
            } else {
                throw new RuntimeException("用户状态异常");
            }
        }

        logger.info("用户 {} 成功登录, 角色: {}", username, user.getRole());
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
} 