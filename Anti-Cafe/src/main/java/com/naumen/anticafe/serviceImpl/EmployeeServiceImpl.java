package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
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
    public List<Employee> getEmployeeList(){
        return employeeRepository.findAll();
    }
    public Employee getEmployee(Long employeeId) throws NotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) throw new NotFoundException("Сотрудник не найден");
        return optionalEmployee.get();
    }
    public List<Role> getAllRole(){
        return roleRepository.findAll();
    }

    public Employee saveEmployee(String name, String username, String password, Long roleId) throws NotFoundException {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode(password));
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty()) throw new NotFoundException("Роль не найдена");;
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
