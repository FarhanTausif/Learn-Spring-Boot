package com.example.RBACwithSpringSecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("")
    public String adminDashboard(Model model) {
        model.addAttribute("message", "Welcome to Admin Dashboard - You have full access!");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("message", "User Management - Only admins can access this");
        return "admin/users";
    }

    @GetMapping("/system")
    public String systemSettings(Model model) {
        model.addAttribute("message", "System Settings - Critical admin functions");
        return "admin/system";
    }
}
