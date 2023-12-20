package com.naumen.anticafe.service.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Page<Employee> getEmployeePage(Pageable pageable);

    List<Employee> getEmployeeList(boolean enabled);

    Employee getEmployee(Long employeeId) throws NotFoundException;
}
