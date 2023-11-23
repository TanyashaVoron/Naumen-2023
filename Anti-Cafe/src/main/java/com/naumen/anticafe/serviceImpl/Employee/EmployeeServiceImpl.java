package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> searchEmployeeDuplicate(String username) {
        return employeeRepository.findByName(username);
    }

    public Employee searchEmployee(String username) throws NotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findByUsername(username);
        if(optionalEmployee.isEmpty()) throw new NotFoundException("Пользователь с таким логином не найден");
        return optionalEmployee.get();
    }
    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeeList(boolean enabled) {
        return employeeRepository.findAllByEnabled(enabled);
    }
    @Override
    @Transactional
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployee(Long employeeId) throws NotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) throw new NotFoundException("Сотрудник не найден");
        return optionalEmployee.get();
    }
}
