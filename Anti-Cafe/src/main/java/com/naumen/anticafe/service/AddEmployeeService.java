package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;

import java.util.List;

public interface AddEmployeeService {
    public Employee saveUser(String name, String username, String password, Long roleId);
    public List<Role> getAllRole();
}
