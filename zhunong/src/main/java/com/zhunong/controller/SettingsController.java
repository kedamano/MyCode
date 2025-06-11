package com.zhunong.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {

    @GetMapping
    public String settings() {
        return "admin/settings";
    }
} 