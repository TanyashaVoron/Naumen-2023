package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.EmployeeService;
import com.naumen.anticafe.validation.RegistrationValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/add")
    public String showAddEmployee(@RequestParam(value = "nameError",required = false) String nameError,
                                  @RequestParam(value = "usernameError",required = false) String usernameError,
                                  @RequestParam(value = "passwordError",required = false) String passwordError,
                                  @AuthenticationPrincipal Employee employee, Model model){
        model.addAttribute("nameError",Optional.ofNullable(nameError));
        model.addAttribute("usernameError",Optional.ofNullable(usernameError));
        model.addAttribute("passwordError",Optional.ofNullable(passwordError));
        model.addAttribute("user",Optional.ofNullable(employee));
        model.addAttribute("roles", employeeService.getAllRole());
        return "employee/addEmployee";
    }
    @GetMapping
    public String showEmployee(@RequestParam(value = "username", required = false) String username,
                               @AuthenticationPrincipal Employee employee,Model model){
        List<Employee> employees;
        if(username!=null&&!username.equals("")) employees = employeeService.getEmployeeUsernameContains(username);
        else  employees = employeeService.getEmployeeList();
        model.addAttribute("employees",employees);
        model.addAttribute("user",Optional.ofNullable(employee));
        return "employee/employee";
    }
    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable("id")Long employeeId,
                           @RequestParam(value = "nameError",required = false) String nameError,
                           @RequestParam(value = "usernameError",required = false) String usernameError,
                           @RequestParam(value = "passwordError",required = false) String passwordError,
                           @AuthenticationPrincipal Employee employee,
                           RedirectAttributes redirectAttributes,
                           Model model){
        try {
            Employee employeeEdit = employeeService.getEmployee(employeeId);
            model.addAttribute("nameError",Optional.ofNullable(nameError));
            model.addAttribute("usernameError",Optional.ofNullable(usernameError));
            model.addAttribute("passwordError",Optional.ofNullable(passwordError));
            model.addAttribute("user",Optional.ofNullable(employee));
            model.addAttribute("employee",employeeEdit);
            for(Role r : employeeEdit.getRole())
                model.addAttribute("employeeRole",r);
            model.addAttribute("roles", employeeService.getAllRole());
            return "employee/editEmployee";
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/add")
    public String addEmployee(@Valid RegistrationValidation registrationValidation,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (FieldError fe :bindingResult.getFieldErrors())
            {
                redirectAttributes.addAttribute(fe.getField()+ "Error",fe.getDefaultMessage());
            }
            return "redirect:/Employee/add";
        }
        try {
            employeeService.saveEmployee(registrationValidation);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/employee";
    }
    @PostMapping("/deactivate")
    public String deactivateEmployee(@RequestParam("employeeId")Long employeeId,RedirectAttributes redirectAttributes){
        try {
            Employee employee = employeeService.getEmployee(employeeId);
            employee.setEnabled(false);
            employeeService.saveEmployee(employee);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/employee";
    }
    @PostMapping("/activate")
    public String activateEmployee(@RequestParam("employeeId")Long employeeId,RedirectAttributes redirectAttributes){
        try {
            Employee employee = employeeService.getEmployee(employeeId);
            employee.setEnabled(true);
            employeeService.saveEmployee(employee);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/employee";
    }
    @PostMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id")Long employeeId,
                               @Valid RegistrationValidation registrationValidation,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (FieldError fe :bindingResult.getFieldErrors())
            {
                redirectAttributes.addAttribute(fe.getField()+ "Error",fe.getDefaultMessage());
            }
            return "redirect:/Employee/edit/"+employeeId;
        }
        try {
            Employee employee = employeeService.getEmployee(employeeId);
            employeeService.updateEmployee(registrationValidation,employee);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/";
    }
}
