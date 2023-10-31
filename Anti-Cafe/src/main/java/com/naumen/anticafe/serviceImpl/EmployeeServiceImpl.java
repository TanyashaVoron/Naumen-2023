package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.repository.RoleRepository;
import com.naumen.anticafe.service.EmployeeService;
import com.naumen.anticafe.validation.RegistrationValidation;
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
    public void saveEmployee(Employee employee){
        employeeRepository.save(employee);
    };
    public Employee getEmployee(Long employeeId) throws NotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) throw new NotFoundException("Сотрудник не найден");
        return optionalEmployee.get();
    }
    public List<Role> getAllRole(){
        return roleRepository.findAll();
    }
    public boolean isAccessOrder(Employee employeeNow,Order order){
        Set<Role> roles = employeeNow.getRole();
        for(Role r:roles){
            if(r.getRole().equals("ROLE_MANAGER")){
                if(!order.getManager().equals(employeeNow)){
                    return false;
                }
            }
        }
        return true;
    }
    private Role getRole(Long roleId) throws NotFoundException {
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty()) throw new NotFoundException("Роль не найдена");
        return optionalRole.get();
    }
    public void updateEmployee(RegistrationValidation registrationValidation, Employee employee) throws NotFoundException {
        employee.setPassword(passwordEncoder.encode(registrationValidation.getPassword()));
        employee.setUsername(registrationValidation.getUsername());
        Set<Role> set = new HashSet<>();
        set.add(getRole(registrationValidation.getRoleId()));
        employee.setRole(set);
        employee.setName(registrationValidation.getName());
        employeeRepository.save(employee);
    }
    public void saveEmployee(RegistrationValidation registrationValidation) throws NotFoundException {
        Role role = getRole(registrationValidation.getRoleId());
        Employee employee = registrationValidation.toEmployee(passwordEncoder,role);
        employeeRepository.save(employee);
    }
    public List<Employee> getEmployeeUsernameContains(String username){
        return employeeRepository.findByUsernameContainsOrderByEnabled(username);
    }
}
