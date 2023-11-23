package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.validation.RegistrationValidation;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee searchEmployeeName(String name) throws NotFoundException;
    Optional<Employee> searchEmployee(String username);
    List<Employee> getEmployeeList(boolean enabled);
    void saveEmployee(Employee employee);
    Employee getEmployee(Long employeeId) throws NotFoundException;

}
