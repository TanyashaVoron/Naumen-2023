package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.DTO.receive.employee.EmployeeDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.service.Employee.RegistrationEmployeeService;
import com.naumen.anticafe.service.Role.RoleService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RegistrationEmployeeServiceImplTest {
    @InjectMocks
    private RegistrationEmployeeServiceImpl registrationEmployeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;

    @SneakyThrows
    @Test
    void registrationEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO("name","username","password",1);
        String passwordEncode = "passwordEncode";
        Role role = new Role(1,"role");
        Mockito.when(passwordEncoder.encode(employeeDTO.password())).thenReturn(passwordEncode);
        Mockito.when(roleService.getRole(employeeDTO.roleId())).thenReturn(role);
        registrationEmployeeService.registrationEmployee(employeeDTO);
        Mockito.verify(employeeRepository).save(Mockito.argThat(employee -> {
            // Проверка на true перед установкой
            assertTrue(employee.isEnabled());
            assertEquals(employeeDTO.name(), employee.getName());
            assertEquals( passwordEncode, employee.getPassword());
            assertEquals(employeeDTO.username(), employee.getUsername());
            assertEquals(employeeDTO.roleId(), employee.getRole().getId());
            return true;
        }));
    }

    @SneakyThrows
    @Test
    void updateEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO("name","username","password",1);
        Employee employee = new Employee();
        String passwordEncode = "passwordEncode";
        Role role = new Role(1,"role");
        Mockito.when(passwordEncoder.encode(employeeDTO.password())).thenReturn(passwordEncode);
        Mockito.when(roleService.getRole(employeeDTO.roleId())).thenReturn(role);
        registrationEmployeeService.updateEmployee(employeeDTO,employee);
        Mockito.verify(employeeRepository).save(Mockito.argThat(employees -> {
            // Проверка на true перед установкой
            assertEquals(employeeDTO.name(), employees.getName());
            assertEquals( passwordEncode,employee.getPassword());
            assertEquals(employeeDTO.username(), employees.getUsername());
            assertEquals(employeeDTO.roleId(), employees.getRole().getId());
            return true;
        }));
    }

    @Test
    void searchEmployeeDuplicate_found_notFound() {
        //true
        String username = "username";
        Mockito.when(employeeRepository.existsByUsername(username)).thenReturn(true);
        Assertions.assertTrue(registrationEmployeeService.searchEmployeeDuplicate(username));
        //false
        username = "user";
        Mockito.when(employeeRepository.existsByUsername(username)).thenReturn(false);
        Assertions.assertFalse(registrationEmployeeService.searchEmployeeDuplicate(username));
    }
}