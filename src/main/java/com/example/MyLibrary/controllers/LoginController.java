package com.example.MyLibrary.controllers;

import com.example.MyLibrary.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "library/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        if (adminService.authenticate(username, password)) {
            return "redirect:/dashboard"; // Redirect to index.html if login is successful
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "library/login"; // Stay on login page if login fails
        }
    }
}