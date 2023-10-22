package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.repository.RoleRepository;
import com.naumen.anticafe.service.AddEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AddEmployeeServiceImpl implements AddEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AddEmployeeServiceImpl(EmployeeRepository employeeRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Role> getAllRole(){
        return roleRepository.findAll();
    }
    public Employee saveUser(String name, String username, String password, Role role){
        Employee employee = new Employee();
        employee.setName(name);
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode(password));
        Set<Role> set = new HashSet<>();
        set.add(role);
        employee.setRole(set);
        employee.setEnabled(true);
        employee.setCredentialsNonExpired(true);
        employee.setAccountNonLocked(true);
        employee.setAccountNonExpired(true);
        return employeeRepository.save(employee);
    }
}
