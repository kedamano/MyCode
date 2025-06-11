package com.zhunong.controller;

import com.zhunong.entity.Account;
import com.zhunong.entity.User;
import com.zhunong.service.AccountService;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public String balance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getByUsername(username);
        Account account = accountService.getByUserId(user.getId());
        model.addAttribute("account", account);
        return "account/balance";
    }
    
    @GetMapping("/balance/check")
    @ResponseBody
    public Map<String, Object> checkBalance(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            BigDecimal balance = accountService.getBalance(user.getId());
            
            result.put("success", true);
            result.put("balance", balance);
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取账户余额失败：" + e.getMessage());
            return result;
        }
    }
} 