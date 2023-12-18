package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.EnabledEmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EnabledEmployeeServiceImpl implements EnabledEmployeeService {
    private final EmployeeRepository employeeRepository;

    public EnabledEmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Устанавливает активность и не активность сотрудника
     */
    @Override
    public void setEnable(Long id, boolean enabled) {
        employeeRepository.setEnable(id, enabled);
    }
}
