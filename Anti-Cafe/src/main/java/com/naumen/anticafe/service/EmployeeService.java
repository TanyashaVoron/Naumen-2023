package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.validation.RegistrationValidation;
import org.springframework.stereotype.Service;

import java.util.List;

public interface EmployeeService {
    boolean saveEmployee(RegistrationValidation registrationValidation) throws NotFoundException;
    void saveEmployee(Employee employee);
    List<Role> getAllRole();
    List<Employee> getEmployeeList(boolean enabled);
    boolean isAccessOrder(Employee employeeNow, Order order);
    Employee getEmployee(Long employeeId) throws NotFoundException;
    List<Employee> getEmployeeUsernameContains(String username);
    void updateEmployee(RegistrationValidation registrationValidation, Employee employee) throws NotFoundException;
}
