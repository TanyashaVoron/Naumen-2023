package com.naumen.anticafe.repository;

import com.naumen.anticafe.domain.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByName(String username);
    List<Employee> findAllByEnabled(boolean enabled);
    List<Employee> findByUsernameContainsOrderByEnabledDesc(String username);
}