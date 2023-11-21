package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.RegistrationEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import com.naumen.anticafe.validation.RegistrationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
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
    public boolean registrationEmployee(RegistrationValidation registrationValidation) throws NotFoundException {
        //если юзернейм уже есть то возвращает фалс
        if (employeeRepository.findByUsername(registrationValidation.getUsername()) != null) return false;
        Role role = roleService.getRole(registrationValidation.getRoleId());
        Employee employee = registrationValidation.toEmployee(passwordEncoder, role);
        employeeRepository.save(employee);
        return true;
    }
    @Override
    @Transactional
    public void updateEmployee(RegistrationValidation registrationValidation, Employee employee) throws NotFoundException {
        employee.setPassword(passwordEncoder.encode(registrationValidation.getPassword()));
        employee.setUsername(registrationValidation.getUsername());
        Set<Role> set = new HashSet<>();
        set.add(roleService.getRole(registrationValidation.getRoleId()));
        employee.setRole(set);
        employee.setName(registrationValidation.getName());
        employeeRepository.save(employee);
    }
}
