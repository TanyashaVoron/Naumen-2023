package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface EmployeeService {
    public Employee saveEmployee(String name, String username, String password, Long roleId) throws NotFoundException;
    public List<Role> getAllRole();
    public List<Employee> getEmployeeList();
    public Employee getEmployee(Long employeeId) throws NotFoundException;
}
