package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.service.AddEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/addEmployee")
public class AddEmployeeController {
    private final AddEmployeeService addEmployeeService;
    @Autowired
    public AddEmployeeController(AddEmployeeService addEmployeeService) {
        this.addEmployeeService = addEmployeeService;
    }

    @GetMapping
    public String addEmployeeShow(@AuthenticationPrincipal Employee employee, Model model){
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        model.addAttribute("roles",addEmployeeService.getAllRole());
        return "addEmployee";
    }
    @PostMapping
    public String addEmployee(@RequestParam("name")String name, @RequestParam("username")String username, @RequestParam("password")String password,  @RequestParam("role")Role role){

        return "redirect:/";
    }
}
