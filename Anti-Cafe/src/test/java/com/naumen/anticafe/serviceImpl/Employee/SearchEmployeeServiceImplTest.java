package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.SearchEmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchEmployeeServiceImplTest {
    @InjectMocks
    private SearchEmployeeServiceImpl searchEmployeeService;
    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void getEmployeeUsernameContains() {
        Role role = new Role(1, "Role");
        String username = "qwe";
        Employee e1 = new Employee(1L, "qwe", "qwe", "qwe", role, true);
        Employee e2 = new Employee(2L, "qwe2", "qwe2", "qwe2", role, true);
        Pageable pageable = PageRequest.of(2, 2);
        Page<Employee> pageE1 = new PageImpl<>(List.of(e1, e2));
        Page<Employee> pageE2 = new PageImpl<>(List.of(e1, e2));
        Mockito.when(employeeRepository.findByUsernameContainsOrderByEnabledDesc(username, pageable)).thenReturn(pageE1);
        Assertions.assertIterableEquals(searchEmployeeService.getEmployeeUsernameContains(username,pageable), pageE2);
        Mockito.verify(employeeRepository, Mockito.times(1)).findByUsernameContainsOrderByEnabledDesc(username, pageable);
    }
}