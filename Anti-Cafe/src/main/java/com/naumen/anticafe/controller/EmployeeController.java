package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/addEmployee")
public class EmployeeController {
    private final EmployeeService employeeService;
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String addEmployeeShow(@AuthenticationPrincipal Employee employee, Model model){
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        model.addAttribute("roles", employeeService.getAllRole());
        return "addEmployee";
    }
    @PostMapping
    public String addEmployee(@RequestParam("name")String name,
                              @RequestParam("username")String username,
                              @RequestParam("password")String password,
                              @RequestParam("role") Long roleId,
                              RedirectAttributes redirectAttributes){
        try {
            employeeService.saveEmployee(name,username,password,roleId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/";
    }
}
