package com.example.RBACwithSpringSecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
public class UserController {

    @GetMapping("")
    public String userDashboard(Model model) {
        model.addAttribute("message", "Welcome to User Dashboard - Your personal workspace!");
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        model.addAttribute("message", "Your Profile - View and edit your information");
        return "user/profile";
    }

    @GetMapping("/tasks")
    public String viewTasks(Model model) {
        model.addAttribute("message", "Your Tasks - View assigned tasks and deadlines");
        return "user/tasks";
    }
}
