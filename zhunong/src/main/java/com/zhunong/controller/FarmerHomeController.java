package com.zhunong.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FarmerHomeController {
    @GetMapping("/farmer/home")
    @PreAuthorize("hasRole('FARMER')")
    public String home() {
        return "farmer/home";
    }
} 