package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchEmployeeServiceImpl implements SearchEmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public SearchEmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * находит сотрудников с имеющимся фрагментом а так же разделяет на страницы
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Employee> getEmployeeUsernameContains(String username, Pageable pageable) {
        return employeeRepository.findByUsernameContainsOrderByEnabledDesc(username, pageable);
    }
}
