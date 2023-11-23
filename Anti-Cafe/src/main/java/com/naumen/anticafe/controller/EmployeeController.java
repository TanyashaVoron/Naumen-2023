package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.object.EmployeeDTO;
import com.naumen.anticafe.DTO.receive.employee.ShowAddEditDTO;
import com.naumen.anticafe.DTO.send.employee.ShowAddSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowEditSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowSendDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.Employee.RegistrationEmployeeService;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import com.naumen.anticafe.serviceImpl.Employee.RegistrationEmployeeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final SearchEmployeeService searchEmployeeService;
    private final RegistrationEmployeeService registrationEmployeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, RoleService roleService, SearchEmployeeService searchEmployeeService, RegistrationEmployeeServiceImpl registrationEmployeeService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.searchEmployeeService = searchEmployeeService;
        this.registrationEmployeeService = registrationEmployeeService;
    }

    @GetMapping("/add")
    public String showAddEmployee(@ModelAttribute ShowAddEditDTO DTO,
                                  @AuthenticationPrincipal(expression = "name") String employee, Model model) {
        if (DTO.getName() == null) DTO.setName("");
        if (DTO.getUsername() == null) DTO.setUsername("");
        if (DTO.getRoleId() == null) DTO.setRoleId(1);
        ShowAddSendDTO sendDTO = new ShowAddSendDTO(
                Optional.ofNullable(DTO.getNameError()),
                Optional.ofNullable(DTO.getUsernameError()),
                Optional.ofNullable(DTO.getUsernameDuplicateError()),
                Optional.ofNullable(DTO.getPasswordError()),
                DTO.getName(),
                DTO.getUsername(),
                DTO.getRoleId(),
                employee,
                roleService.getAllRole()
        );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/addEmployee";
    }

    @GetMapping
    public String showEmployee(@RequestParam(value = "username", required = false) String username,
                               @AuthenticationPrincipal(expression = "name") String employee, Model model) {
        ShowSendDTO sendDTO = new ShowSendDTO();
        //проверяет юзер нейм
        sendDTO.setNameEmployee(employee);
        if (username != null && !username.equals("")) {
            //если не пустой, то выискивает сотрудников с фрагментом
            for (Employee e : searchEmployeeService.getEmployeeUsernameContains(username)) {
                sendDTO.setEmployeeDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
            }
        } else {
            //если пустой сначала выискивает активные а после добавляет не активные
            for (Employee e : employeeService.getEmployeeList(true)) {
                sendDTO.setEmployeeDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
            }
            for (Employee e : employeeService.getEmployeeList(false)) {
                sendDTO.setEmployeeDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
            }
        }
        model.addAttribute("sendDTO", sendDTO);
        return "employee/employee";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable("id") Long employeeId,
                           @ModelAttribute ShowAddEditDTO DTO,
                           @AuthenticationPrincipal(expression = "name") String employee,
                           Model model) throws NotFoundException {
        Employee employeeEdit = employeeService.getEmployee(employeeId);
        ShowEditSendDTO sendDTO = new ShowEditSendDTO(
                Optional.ofNullable(DTO.getNameError()),
                Optional.ofNullable(DTO.getUsernameError()),
                Optional.ofNullable(DTO.getPasswordError()),
                employee,
                employeeEdit.getId(),
                employeeEdit.getName(),
                employeeEdit.getUsername(),
                employeeEdit.getRole().getId(),
                roleService.getAllRole()
        );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/editEmployee";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute EmployeeDTO DTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        //если есть ошибки в валидации то переадресует все ошибки в GET
        Optional<Employee> employee = employeeService.searchEmployee(DTO.getUsername());
        if (employee.isPresent())
            bindingResult.addError(new FieldError("addDTO", "usernameDuplicate", "Имя пользователя уже занято"));
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            redirectAttributes.addAttribute("name", DTO.getName());
            redirectAttributes.addAttribute("username", DTO.getUsername());
            return "redirect:/employee/add";
        }
        registrationEmployeeService.registrationEmployee(DTO);
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
                               @Valid @ModelAttribute EmployeeDTO DTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) throws NotFoundException {
        Employee employee = employeeService.getEmployee(employeeId);
        Optional<Employee> optionalEmployee = employeeService.searchEmployee(DTO.getUsername());
        if (optionalEmployee.isPresent())
            if(!optionalEmployee.get().getName().equals(employee.getName()))
                bindingResult.addError(new FieldError("addDTO", "usernameDuplicate", "Имя пользователя уже занято"));
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            redirectAttributes.addAttribute("name", DTO.getName());
            redirectAttributes.addAttribute("username", DTO.getUsername());
            return "redirect:/employee/edit/"+employeeId;
        }
        //находит и обновляет сотрудника
        registrationEmployeeService.updateEmployee(DTO, employee);
        return "redirect:/";
    }
}
