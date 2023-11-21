package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class SearchEmployeeServiceImpl implements SearchEmployeeService {
    private final EmployeeRepository employeeRepository;
    @Autowired
    public SearchEmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeeUsernameContains(String username) {
        return employeeRepository.findByUsernameContainsOrderByEnabledDesc(username);
    }
}
