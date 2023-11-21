package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;

import java.util.List;

public interface SearchEmployeeService {
    List<Employee> getEmployeeUsernameContains(String username);
}
