package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Employees;
import org.springframework.data.repository.CrudRepository;

public interface EmployeesRepository extends CrudRepository<Employees, Long> {
}