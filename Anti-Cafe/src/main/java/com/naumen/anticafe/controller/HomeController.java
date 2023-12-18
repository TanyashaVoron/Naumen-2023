package com.naumen.anticafe.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String homeShow(@AuthenticationPrincipal(expression = "name") String employee, Model model) {
        model.addAttribute("user", employee);
        return "home";
    }
}
