package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchEmployeeService {
    Page<Employee> getEmployeeUsernameContains(String username, Pageable pageable);
}
