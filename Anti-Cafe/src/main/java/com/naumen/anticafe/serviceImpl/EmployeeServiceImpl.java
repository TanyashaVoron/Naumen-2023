package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.repository.RoleRepository;
import com.naumen.anticafe.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Role> getAllRole(){
        return roleRepository.findAll();
    }
    public Employee saveEmployee(String name, String username, String password, Long roleId){
        Employee employee = new Employee();
        employee.setName(name);
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode(password));
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty()) return null;
        Set<Role> set = new HashSet<>();
        set.add(optionalRole.get());
        employee.setRole(set);
        employee.setEnabled(true);
        employee.setCredentialsNonExpired(true);
        employee.setAccountNonLocked(true);
        employee.setAccountNonExpired(true);
        return employeeRepository.save(employee);
    }
}
