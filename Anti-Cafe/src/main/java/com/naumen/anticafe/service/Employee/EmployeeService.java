package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> searchEmployeeDuplicate(String name);
    Employee searchEmployee(String username) throws NotFoundException;
    List<Employee> getEmployeeList(boolean enabled);
    void saveEmployee(Employee employee);
    Employee getEmployee(Long employeeId) throws NotFoundException;

}
