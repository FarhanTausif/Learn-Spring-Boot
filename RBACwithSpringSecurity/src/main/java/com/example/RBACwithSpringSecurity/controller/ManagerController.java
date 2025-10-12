package com.example.RBACwithSpringSecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ManagerController {

    @GetMapping("")
    public String managerDashboard(Model model) {
        model.addAttribute("message", "Welcome to Manager Dashboard - Manage your team!");
        return "manager/dashboard";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("message", "Team Reports - Manager level access");
        return "manager/reports";
    }

    @GetMapping("/team")
    public String manageTeam(Model model) {
        model.addAttribute("message", "Team Management - Assign tasks and monitor progress");
        return "manager/team";
    }
}
