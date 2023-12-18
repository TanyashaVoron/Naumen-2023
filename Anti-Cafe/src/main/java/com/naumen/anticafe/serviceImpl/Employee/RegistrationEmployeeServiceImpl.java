package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.DTO.receive.employee.EmployeeDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.RegistrationEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationEmployeeServiceImpl implements RegistrationEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public RegistrationEmployeeServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    /**
     * Регистрирует сотрудника
     */
    @Override
    @Transactional
    public void registrationEmployee(EmployeeDTO employeeDTO) throws NotFoundException {
        Employee employee = new Employee();
        employee.setEnabled(true);
        updateEmployee(employeeDTO, employee);
    }

    /**
     * обновляет сотрудника
     */
    @Override
    @Transactional
    public void updateEmployee(EmployeeDTO employeeDTO, Employee employee) throws NotFoundException {
        employee.setPassword(passwordEncoder.encode(employeeDTO.password()));
        employee.setUsername(employeeDTO.username());
        Role role = roleService.getRole(employeeDTO.roleId());
        employee.setRole(role);
        employee.setName(employeeDTO.name());
        employeeRepository.save(employee);
    }

    /**
     * проверяет есть ли сотрудник с таким логином
     */
    @Override
    @Transactional(readOnly = true)
    public boolean searchEmployeeDuplicate(String username) {
        return employeeRepository.existsByUsername(username);
    }

}
