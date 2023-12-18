package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.DTO.receive.employee.EmployeeDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.exception.NotFoundException;

public interface RegistrationEmployeeService {
    void registrationEmployee(EmployeeDTO employeeDTO) throws NotFoundException;

    boolean searchEmployeeDuplicate(String name);

    void updateEmployee(EmployeeDTO employeeDTO, Employee employee) throws NotFoundException;
}
