package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.EnabledEmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EnabledEmployeeServiceImplTest {
    @InjectMocks
    private EnabledEmployeeServiceImpl enabledEmployeeServiceImpl;
    @Mock
    private EmployeeRepository employeeRepository;
    @Test
    void setEnable_true() {
        Long id = 1L;
        boolean enabled = true;
        enabledEmployeeServiceImpl.setEnable(id,enabled);
        Mockito.verify(employeeRepository,Mockito.times(1)).setEnable(id,enabled);
    }
    @Test
    void setEnable_false() {
        Long id = 2L;
        boolean enabled = false;
        enabledEmployeeServiceImpl.setEnable(id,enabled);
        Mockito.verify(employeeRepository,Mockito.times(1)).setEnable(id,enabled);
    }
}