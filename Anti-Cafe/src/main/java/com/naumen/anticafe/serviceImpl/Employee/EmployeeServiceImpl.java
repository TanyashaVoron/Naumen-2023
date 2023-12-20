package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * находит всех сотрудников и разделяет на страницы
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Employee> getEmployeePage(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    /**
     * передает список сотрудников с указанным полем Enabled
     */
    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeeList(boolean enabled) {
        return employeeRepository.findAllByEnabled(enabled);
    }

    /**
     * ищет сотрудника оп Id
     */
    @Override
    @Transactional(readOnly = true)
    public Employee getEmployee(Long employeeId) throws NotFoundException {
        //если не нашел выбрасывает ошибку
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) throw new NotFoundException("Сотрудник не найден");
        return optionalEmployee.get();
    }
}
