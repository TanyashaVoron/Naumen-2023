package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.employee.*;
import com.naumen.anticafe.DTO.send.employee.ShowAddSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowEditSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowSendDTO;
import com.naumen.anticafe.converter.ConverterEmployee;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.properties.PageProperties;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.Employee.EnabledEmployeeService;
import com.naumen.anticafe.service.Employee.RegistrationEmployeeService;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import com.naumen.anticafe.serviceImpl.Employee.RegistrationEmployeeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final SearchEmployeeService searchEmployeeService;
    private final RegistrationEmployeeService registrationEmployeeService;
    private final PageProperties pageProperties;
    private final EnabledEmployeeService enabledEmployeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              RoleService roleService,
                              SearchEmployeeService searchEmployeeService,
                              RegistrationEmployeeServiceImpl registrationEmployeeService,
                              PageProperties pageProperties,
                              EnabledEmployeeService enabledEmployeeService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.searchEmployeeService = searchEmployeeService;
        this.registrationEmployeeService = registrationEmployeeService;
        this.pageProperties = pageProperties;
        this.enabledEmployeeService = enabledEmployeeService;
    }

    /**
     * отображет форму добавления сотрудников, где указываеться ошибки добавляения
     */
    @GetMapping("/add")
    @Transactional(readOnly = true)
    public String showAddEmployee(@ModelAttribute ShowAddDTO dto,
                                  @AuthenticationPrincipal(expression = "name") String employee, Model model) {
        ShowAddSendDTO sendDTO = ConverterEmployee.convertToShowAddSendDTO(dto, employee, roleService.getAllRole());
        model.addAttribute("sendDTO", sendDTO);
        return "employee/addEmployee";
    }

    /**
     * отображает всех сотрудников либо тех у кого есть в имени фрагмент из поиска
     */
    @GetMapping
    @Transactional(readOnly = true)
    public String showEmployee(@ModelAttribute ShowDTO dto,
                               @AuthenticationPrincipal(expression = "name") String employee,
                               Model model) {
        //Создает страницы сотрудников
        Page<Employee> employeeSendDTOList;
        if (dto.username() != null && !dto.username().isEmpty()) {
            //в случае если указан параметра поиска
            Pageable pageable = PageRequest.of(dto.page() - 1, pageProperties.getPageSize());
            employeeSendDTOList = searchEmployeeService.getEmployeeUsernameContains(dto.username(), pageable);
        } else {
            //если поиск не указан то возвращает всех сначала отсортированых по активности потом по ИД вначале идут
            // активные
            Pageable pageable = PageRequest.of(dto.page() - 1,
                    pageProperties.getPageSize(),
                    Sort
                            .by("enabled")
                            .descending()
                            .and(Sort.by("id"))
            );
            employeeSendDTOList = employeeService.getEmployeePage(pageable);
        }
        ShowSendDTO sendDTO = ConverterEmployee
                .convertToShowSendDTO(employee,
                        employeeSendDTOList,
                        dto.username()
                );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/employee";
    }

    /**
     * Отображает и заполняет форму для реодактирования сотрудника, а так же показывает ошибки в форме
     */
    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEdit(@PathVariable("id") Long employeeId,
                           @ModelAttribute ShowEditDTO dto,
                           @AuthenticationPrincipal(expression = "name") String employee,
                           Model model) throws NotFoundException {
        Employee employeeEdit = employeeService.getEmployee(employeeId);
        ShowEditSendDTO sendDTO = ConverterEmployee
                .convertToShowEditSendDTO(dto,
                        employee,
                        employeeEdit,
                        roleService.getAllRole()
                );
        model.addAttribute("sendDTO", sendDTO);
        return "employee/editEmployee";
    }

    /**
     * Добавляет сотрудника в бд если есть ошбки выводит их в форму
     */
    @PostMapping("/add")
    @Transactional
    public String addEmployee(@Valid @ModelAttribute EmployeeDTO dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        //проверяет есть ли такие же имена
        if (registrationEmployeeService.searchEmployeeDuplicate(dto.username()))
            //если есть добавляет в BindingResult ошибку об этом
            bindingResult.addError(new FieldError("addDTO",
                    "usernameDuplicate",
                    "Имя пользователя уже занято")
            );
        //проверяет ошибку валидации
        if (bindingResult.hasErrors()) {
            //если нашел то добавляет все ошибки в переадресацию
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            //добавляет в переадресацию так же и поля имени и логина для автозаполнения в форме
            redirectAttributes.addAttribute("name", dto.name());
            redirectAttributes.addAttribute("username", dto.username());
            return "redirect:/employee/add";
        }
        //регестрирует сотрудника
        registrationEmployeeService.registrationEmployee(dto);
        return "redirect:/employee";
    }

    @PostMapping("/deactivate")
    @Transactional
    public String deactivateEmployee(@ModelAttribute DeactivateDTO dto) {
        enabledEmployeeService.setEnable(dto.employeeId(), false);
        return "redirect:/employee";
    }

    @PostMapping("/activate")
    @Transactional
    public String activateEmployee(@ModelAttribute ActivateDTO dto) {
        enabledEmployeeService.setEnable(dto.employeeId(), true);
        return "redirect:/employee";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String editEmployee(@PathVariable("id") Long employeeId,
                               @Valid @ModelAttribute EmployeeDTO dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) throws NotFoundException {
        Employee employee = employeeService.getEmployee(employeeId);
        if (registrationEmployeeService.searchEmployeeDuplicate(dto.username()))
            if (!dto.username().equals(employee.getName()))
                bindingResult.addError(new FieldError("addDTO",
                        "usernameDuplicate",
                        "Имя пользователя уже занято")
                );
        if (bindingResult.hasErrors()) {
            for (FieldError fe : bindingResult.getFieldErrors()) {
                redirectAttributes.addAttribute(fe.getField() + "Error", fe.getDefaultMessage());
            }
            redirectAttributes.addAttribute("name", dto.name());
            redirectAttributes.addAttribute("username", dto.username());
            return "redirect:/employee/edit/" + employeeId;
        }
        registrationEmployeeService.updateEmployee(dto, employee);
        return "redirect:/";
    }
}
