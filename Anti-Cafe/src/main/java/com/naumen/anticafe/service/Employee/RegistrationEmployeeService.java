package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.validation.RegistrationValidation;

public interface RegistrationEmployeeService {
    boolean registrationEmployee(RegistrationValidation registrationValidation) throws NotFoundException;
    void updateEmployee(RegistrationValidation registrationValidation, Employee employee) throws NotFoundException;
}
