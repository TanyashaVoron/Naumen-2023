package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.DTO.object.EmployeeDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
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

    @Override
    @Transactional
    public void registrationEmployee(EmployeeDTO employeeDTO) throws NotFoundException {
        Employee employee = new Employee();
        updateEmployee(employeeDTO,employee);
    }
    @Override
    @Transactional
    public void updateEmployee(EmployeeDTO employeeDTO, Employee employee) throws NotFoundException {
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee.setUsername(employeeDTO.getUsername());
        Role role = roleService.getRole(employeeDTO.getRoleId());
        employee.setRole(role);
        employee.setName(employeeDTO.getName());
        employeeRepository.save(employee);
    }
}
