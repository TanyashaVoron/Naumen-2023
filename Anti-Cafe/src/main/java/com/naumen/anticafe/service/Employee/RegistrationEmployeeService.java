package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.DTO.object.EmployeeDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;

public interface RegistrationEmployeeService {
    void registrationEmployee(EmployeeDTO employeeDTO) throws NotFoundException;
    void updateEmployee(EmployeeDTO employeeDTO, Employee employee) throws NotFoundException;
}
