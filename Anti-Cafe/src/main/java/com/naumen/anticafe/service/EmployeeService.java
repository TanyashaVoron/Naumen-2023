package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import org.springframework.stereotype.Service;

import java.util.List;

public interface EmployeeService {
    public Employee saveEmployee(String name, String username, String password, Long roleId);
    public List<Role> getAllRole();
}
