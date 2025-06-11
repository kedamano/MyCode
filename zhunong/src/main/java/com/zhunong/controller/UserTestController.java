package com.zhunong.controller;

import com.zhunong.entity.User;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/user-test")
public class UserTestController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/create-test-users")
    public String createTestUsers() {
        try {
            // 创建测试职工
            User employee = new User();
            employee.setUsername("employee");
            employee.setPassword("employee123");
            employee.setRealName("测试职工");
            employee.setPhone("13900000001");
            employee.setRole("EMPLOYEE");
            employee.setStatus(1); // 正常状态
            
            // 创建测试农户
            User farmer = new User();
            farmer.setUsername("farmer");
            farmer.setPassword("farmer123");
            farmer.setRealName("测试农户");
            farmer.setPhone("13900000002");
            farmer.setRole("FARMER");
            farmer.setStatus(1); // 正常状态
            
            // 注册用户
            if (userService.getByUsername("employee") == null) {
                userService.register(employee);
            }
            
            if (userService.getByUsername("farmer") == null) {
                userService.register(farmer);
            }
            
            return "测试用户创建成功";
        } catch (Exception e) {
            return "创建失败: " + e.getMessage();
        }
    }
} 