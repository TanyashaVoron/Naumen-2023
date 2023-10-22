package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    Employee findByUsername(String username);
}