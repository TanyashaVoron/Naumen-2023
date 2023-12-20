package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import lombok.SneakyThrows;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Test
    void getEmployeeList() {
        Role role = new Role(1,"Role");
        Employee e1 = new Employee(1L,"qwe","qwe","qwe",role,true);
        Employee e2 = new Employee(2L,"qwe2","qwe2","qwe2",role,true);
        Pageable pageable = PageRequest.of(2,2);
        Page<Employee> pageE1 = new PageImpl<>(List.of(e1,e2));
        Page<Employee> pageE2 = new PageImpl<>(List.of(e1,e2));
        Mockito.when(employeeRepository.findAll(pageable)).thenReturn(pageE1);
        Assertions.assertIterableEquals(employeeService.getEmployeePage(pageable),pageE2);
        Mockito.verify(employeeRepository,Mockito.times(1)).findAll(pageable);
    }

    @Test
    void testGetEmployeeList_false() {
        Role role = new Role(1,"Role");
        Employee e1 = new Employee(1L,"qwe","qwe","qwe",role,false);
        List<Employee> employeeList1 = new ArrayList<>(List.of(e1));
        List<Employee> employeeList2 = new ArrayList<>(List.of(e1));
        Mockito.when(employeeRepository.findAllByEnabled(false)).thenReturn(employeeList1);
        Assertions.assertIterableEquals(employeeService.getEmployeeList(false),employeeList2);
        Mockito.verify(employeeRepository,Mockito.times(1)).findAllByEnabled(false);
    }
    @Test
    void testGetEmployeeList_true() {
        Role role = new Role(1,"Role");
        Employee e1 = new Employee(1L,"qwe","qwe","qwe",role,true);
        List<Employee> employeeList1 = new ArrayList<>(List.of(e1));
        List<Employee> employeeList2 = new ArrayList<>(List.of(e1));
        Mockito.when(employeeRepository.findAllByEnabled(true)).thenReturn(employeeList1);
        Assertions.assertIterableEquals(employeeService.getEmployeeList(true),employeeList2);
        Mockito.verify(employeeRepository,Mockito.times(1)).findAllByEnabled(true);
    }

    @SneakyThrows
    @Test
    void getEmployee_found_andException() {
        Role role = new Role(1,"Role");
        Employee e1 = new Employee(1L,"qwe","qwe","qwe",role,true);
        Long id = 1L;
        Long exceptionId = 2L;
        //найден
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(e1));
        Assertions.assertEquals(employeeService.getEmployee(id),e1);
        Mockito.verify(employeeRepository,Mockito.times(1)).findById(id);
        //ненайден
        Mockito.when(employeeRepository.findById(exceptionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,()->employeeService.getEmployee(exceptionId));
        Mockito.verify(employeeRepository,Mockito.times(1)).findById(exceptionId);
    }
}