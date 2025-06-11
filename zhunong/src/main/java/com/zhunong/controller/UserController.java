package com.zhunong.controller;

import com.zhunong.entity.User;
import com.zhunong.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody User user) {
        try {
            logger.debug("收到注册请求：username={}, role={}, address={}", 
                    user.getUsername(), user.getRole(), user.getAddress());
            userService.register(user);
            logger.debug("注册成功：username={}, id={}, role={}, status={}", 
                    user.getUsername(), user.getId(), user.getRole(), user.getStatus());
            return "注册成功";
        } catch (Exception e) {
            logger.error("注册失败：" + e.getMessage(), e);
            return "注册失败：" + e.getMessage();
        }
    }

    @PostMapping("/employee/create")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String createEmployee(@RequestBody User user) {
        try {
            userService.createEmployee(user);
            return "创建成功";
        } catch (Exception e) {
            return "创建失败：" + e.getMessage();
        }
    }

    @PostMapping("/farmer/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String reviewFarmer(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            logger.debug("审核农户：id={}, approved={}", id, approved);
            userService.reviewFarmer(id, approved);
            return "审核完成";
        } catch (Exception e) {
            logger.error("审核失败：" + e.getMessage(), e);
            return "审核失败：" + e.getMessage();
        }
    }

    @GetMapping("/admin/farmers")
    @PreAuthorize("hasRole('ADMIN')")
    public String listFarmers(Model model) {
        model.addAttribute("farmers", userService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
        ));
        return "admin/farmers";
    }

    @GetMapping("/admin/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String listEmployees(Model model) {
        model.addAttribute("employees", userService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getRole, "EMPLOYEE")
        ));
        return "admin/employees";
    }

    @GetMapping("/profile")
    public String profile(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getByUsername(username);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User dbUser = userService.getByUsername(username);
        dbUser.setRealName(user.getRealName());
        dbUser.setPhone(user.getPhone());
        dbUser.setEmail(user.getEmail());
        dbUser.setAddress(user.getAddress());
        userService.updateById(dbUser);
        model.addAttribute("user", dbUser);
        model.addAttribute("success", true);
        return "user/profile";
    }

    // 农户信息维护页面
    @GetMapping("/farmer/profile")
    @PreAuthorize("hasRole('FARMER')")
    public String farmerProfilePage(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails, org.springframework.ui.Model model) {
        String username = userDetails.getUsername();
        User user = userService.getByUsername(username);
        model.addAttribute("user", user);
        return "user/farmer/profile";
    }

    // 农户信息编辑提交
    @PostMapping("/farmer/profile")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String updateFarmerProfile(@ModelAttribute User user, @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            User dbUser = userService.getByUsername(username);
            dbUser.setRealName(user.getRealName());
            dbUser.setPhone(user.getPhone());
            dbUser.setEmail(user.getEmail());
            dbUser.setAddress(user.getAddress());
            // 银行账户字段暂不保存，等待添加到实体类
            
            userService.updateById(dbUser);
            return "更新成功";
        } catch (Exception e) {
            return "更新失败：" + e.getMessage();
        }
    }
} 