package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String homeShow(@AuthenticationPrincipal Employee employee, Model model){
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        return "home";
    }
}
