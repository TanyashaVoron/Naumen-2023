package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.employee.ShowAddEmployeeDTO;
import com.naumen.anticafe.DTO.send.employee.ShowAddEmployeeSendDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import com.naumen.anticafe.serviceImpl.Employee.RegistrationEmployeeServiceImpl;
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
    private final RoleService roleService;
    private final SearchEmployeeService searchEmployeeService;
    private final RegistrationEmployeeServiceImpl registrationEmployeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, RoleService roleService, SearchEmployeeService searchEmployeeService, RegistrationEmployeeServiceImpl registrationEmployeeService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.searchEmployeeService = searchEmployeeService;
        this.registrationEmployeeService = registrationEmployeeService;
    }

    @GetMapping("/add")
    public String showAddEmployee(@ModelAttribute ShowAddEmployeeDTO DTO,
                                  @AuthenticationPrincipal(expression = "name") String employee, Model model) {
        ShowAddEmployeeSendDTO sendDTO = new ShowAddEmployeeSendDTO(
                Optional.ofNullable(DTO.getNameError()),
                Optional.ofNullable(DTO.getUsernameError()),
                Optional.ofNullable(DTO.getPasswordError()),
                Optional.ofNullable(employee),
                roleService.getAllRole()
        );
        model.addAttribute("send",sendDTO);
        return "employee/addEmployee";
    }

    @GetMapping
    public String showEmployee(@RequestParam(value = "username", required = false) String username,
                               @AuthenticationPrincipal Employee employee, Model model) {
        List<Employee> employees;
        //проверяет юзер нейм
        if (username != null && !username.equals("")) {
            //если не пустой, то выискивает сотрудников с фрагментом
            employees = searchEmployeeService.getEmployeeUsernameContains(username);
        } else {
            //если пустой сначала выискивает активные а после добавляет не активные
            employees = employeeService.getEmployeeList(true);
            employees.addAll(employeeService.getEmployeeList(false));
        }
        model.addAttribute("employees", employees);
        model.addAttribute("user", Optional.ofNullable(employee));
        return "employee/employee";
    }
    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable("id") Long employeeId,
                           @RequestParam(value = "nameError", required = false) String nameError,
                           @RequestParam(value = "usernameError", required = false) String usernameError,
                           @RequestParam(value = "passwordError", required = false) String passwordError,
                           @AuthenticationPrincipal Employee employee,
                           RedirectAttributes redirectAttributes,
                           Model model) throws NotFoundException {
            Employee employeeEdit = employeeService.getEmployee(employeeId);
            model.addAttribute("nameError", Optional.ofNullable(nameError));
            model.addAttribute("usernameError", Optional.ofNullable(usernameError));
            model.addAttribute("passwordError", Optional.ofNullable(passwordError));
            model.addAttribute("user", Optional.ofNullable(employee));
            model.addAttribute("employee", employeeEdit);
            //добавляет роль пользователя
            for (Role r : employeeEdit.getRole())
                model.addAttribute("employeeRole", r);
            model.addAttribute("roles", roleService.getAllRole());
            return "employee/editEmployee";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid RegistrationValidation registrationValidation,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        //если есть ошибки в валидации то переадресует все ошибки в GET
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            return "redirect:/employee/add";
        }
            //сохраняет пользователя в бд, но если есть такой юзернейм передает ошибку в GET
            if (!registrationEmployeeService.registrationEmployee(registrationValidation)) {
                redirectAttributes.addAttribute("usernameError", "Пользователь уже существует");
                return "redirect:/employee/add";
            }
            return "redirect:/employee";
    }

    @PostMapping("/deactivate")
    public String deactivateEmployee(@RequestParam("employeeId") Long employeeId, RedirectAttributes redirectAttributes) throws NotFoundException {
            Employee employee = employeeService.getEmployee(employeeId);
            employee.setEnabled(false);
            employeeService.saveEmployee(employee);
            return "redirect:/employee";
    }

    @PostMapping("/activate")
    public String activateEmployee(@RequestParam("employeeId") Long employeeId, RedirectAttributes redirectAttributes) throws NotFoundException {
            Employee employee = employeeService.getEmployee(employeeId);
            employee.setEnabled(true);
            employeeService.saveEmployee(employee);
            return "redirect:/employee";
    }
    @PostMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Long employeeId,
                               @Valid RegistrationValidation registrationValidation,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) throws NotFoundException {
        //если есть ошибки в валидации то переадресует все ошибки в GET
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            return "redirect:/Employee/edit/" + employeeId;
        }
            //находит и обновляет сотрудника
            Employee employee = employeeService.getEmployee(employeeId);
            registrationEmployeeService.updateEmployee(registrationValidation, employee);
            return "redirect:/";
    }
}
