package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.employee.EmployeeDTO;
import com.naumen.anticafe.DTO.receive.employee.*;
import com.naumen.anticafe.DTO.send.employee.EmployeeSendDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
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
    @Transactional(readOnly = true)
    public String showAddEmployee(@ModelAttribute ShowAddDTO dto,
                                  @AuthenticationPrincipal(expression = "name") String employee, Model model) {
        ShowAddSendDTO sendDTO = new ShowAddSendDTO(
                Optional.ofNullable(dto.nameError()),
                Optional.ofNullable(dto.usernameError()),
                Optional.ofNullable(dto.usernameDuplicateError()),
                Optional.ofNullable(dto.passwordError()),
                dto.name()==null?"":dto.name(),
                dto.username()==null?"":dto.username(),
                dto.roleId()==null?1:dto.roleId(),
                employee,
                roleService.getAllRole()
        );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/addEmployee";
    }
    @GetMapping
    @Transactional(readOnly = true)
    public String showEmployee(@ModelAttribute ShowDTO dto,
                               @AuthenticationPrincipal(expression = "name") String employee,
                               Model model) {
        List<EmployeeSendDTO> employeeSendDTOList = new ArrayList<>();
        if (dto.username()!= null && !dto.username().isEmpty()) {
            //если не пустой, то выискивает сотрудников с фрагментом
            for (Employee e : searchEmployeeService.getEmployeeUsernameContains(dto.username())) {
                EmployeeSendDTO employeeSendDTO = new EmployeeSendDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
                employeeSendDTOList.add(employeeSendDTO);
            }
        } else {
            //если пустой сначала выискивает активные а после добавляет не активные
            for (Employee e : employeeService.getEmployeeList(true)) {
                EmployeeSendDTO employeeSendDTO = new EmployeeSendDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
                employeeSendDTOList.add(employeeSendDTO);
            }
            for (Employee e : employeeService.getEmployeeList(false)) {
                EmployeeSendDTO employeeSendDTO = new EmployeeSendDTO(
                        e.getId(),
                        e.getName(),
                        e.getUsername(),
                        e.getRole().getRole(),
                        e.isEnabled()
                );
                employeeSendDTOList.add(employeeSendDTO);
            }
        }
        ShowSendDTO sendDTO = new ShowSendDTO(employee,employeeSendDTOList);
        model.addAttribute("sendDTO", sendDTO);
        return "employee/employee";
    }

    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEdit(@PathVariable("id") Long employeeId,
                           @ModelAttribute ShowEditDTO dto,
                           @AuthenticationPrincipal(expression = "name") String employee,
                           Model model) throws NotFoundException {
        Employee employeeEdit = employeeService.getEmployee(employeeId);
        ShowEditSendDTO sendDTO = new ShowEditSendDTO(
                Optional.ofNullable(dto.nameError()),
                Optional.ofNullable(dto.username()),
                Optional.ofNullable(dto.usernameDuplicateError()),
                Optional.ofNullable(dto.passwordError()),
                employee,
                employeeEdit.getId(),
                employeeEdit.getUsername(),
                employeeEdit.getRole().getId(),
                employeeEdit.getName(),
                roleService.getAllRole()
        );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/editEmployee";
    }

    @PostMapping("/add")
    @Transactional
    public String addEmployee(@Valid @ModelAttribute EmployeeDTO dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        //если есть ошибки в валидации то переадресует все ошибки в GET
        Optional<Employee> employee = employeeService.searchEmployeeDuplicate(dto.username());
        if (employee.isPresent())
            bindingResult.addError(new FieldError("addDTO", "usernameDuplicate", "Имя пользователя уже занято"));
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            redirectAttributes.addAttribute("name", dto.name());
            redirectAttributes.addAttribute("username", dto.username());
            return "redirect:/employee/add";
        }
        registrationEmployeeService.registrationEmployee(dto);
        return "redirect:/employee";
    }

    @PostMapping("/deactivate")
    @Transactional
    public String deactivateEmployee(@ModelAttribute DeactivateDTO dto) throws NotFoundException {
        Employee employee = employeeService.getEmployee(dto.employeeId());
        employee.setEnabled(false);
        employeeService.saveEmployee(employee);
        return "redirect:/employee";
    }

    @PostMapping("/activate")
    @Transactional
    public String activateEmployee(@ModelAttribute ActivateDTO DTO, RedirectAttributes redirectAttributes) throws NotFoundException {
        Employee employee = employeeService.getEmployee(DTO.employeeId());
        employee.setEnabled(true);
        employeeService.saveEmployee(employee);
        return "redirect:/employee";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String editEmployee(@PathVariable("id") Long employeeId,
                               @Valid @ModelAttribute EmployeeDTO dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) throws NotFoundException {
        Employee employee = employeeService.getEmployee(employeeId);
        Optional<Employee> optionalEmployee = employeeService.searchEmployeeDuplicate(dto.username());
        if (optionalEmployee.isPresent())
            if(!optionalEmployee.get().getName().equals(employee.getName()))
                bindingResult.addError(new FieldError("addDTO", "usernameDuplicate", "Имя пользователя уже занято"));
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            redirectAttributes.addAttribute("name", dto.name());
            redirectAttributes.addAttribute("username", dto.username());
            return "redirect:/employee/edit/"+employeeId;
        }
        //находит и обновляет сотрудника
/*        registrationEmployeeService.updateEmployee(DTO, employee);*/
        return "redirect:/";
    }
}
