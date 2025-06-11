package com.zhunong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/products")
    public String redirectToProductsList() {
        return "redirect:/admin/products/list";
    }
    
    @GetMapping("/admin/products")
    public String redirectToAdminProductsList() {
        return "redirect:/admin/products/list";
    }
} 