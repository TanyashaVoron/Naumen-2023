package com.naumen.anticafe.serviceImpl.Employee;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.EmployeeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @BeforeEach
    void init(){

    }
    @Test
    void searchEmployeeDuplicate_Search() {
        String name1 = "employeeFound1";
        String name2 = "employeeFound2";
        Employee employee1 = new Employee(1l,"name","qwe",name1,new Role(),false);
        Employee employee2 = new Employee(1l,"name","qwe",name2,new Role(),false);
        Mockito.when(employeeRepository.findByUsername(name1)).thenReturn(Optional.of(employee1));
        Mockito.when(employeeRepository.findByUsername(name2)).thenReturn(Optional.of(employee2));
        Optional<Employee> optionalEmployee1 = employeeService.searchEmployeeDuplicate(name1);
        Optional<Employee> optionalEmployee2 = employeeService.searchEmployeeDuplicate(name2);
        Assertions.assertEquals(optionalEmployee1.get().getUsername(),name1);
        Assertions.assertEquals(optionalEmployee2.get().getUsername(),name2);
    }

    @Test
    void searchEmployeeDuplicate_NotFound() {
        String name1 = "employeeNotFound1";
        String name2 = "employeeNotFound2";
        Mockito.when(employeeRepository.findByUsername(name1)).thenReturn(Optional.empty());
        Mockito.when(employeeRepository.findByUsername(name2)).thenReturn(Optional.empty());
        Optional<Employee> optionalEmployee1 = employeeService.searchEmployeeDuplicate(name1);
        Optional<Employee> optionalEmployee2 = employeeService.searchEmployeeDuplicate(name2);
        Assertions.assertTrue(optionalEmployee1.isEmpty());
        Assertions.assertTrue(optionalEmployee2.isEmpty());
    }
    @SneakyThrows
    @Test
    void searchEmployee_search() {
        String name1 = "employeeFound1";
        String name2 = "employeeFound2";
        Employee employee1 = new Employee(1l,"name","qwe",name1,new Role(),false);
        Employee employee2 = new Employee(1l,"name","qwe",name2,new Role(),false);
        Mockito.when(employeeRepository.findByUsername(name1)).thenReturn(Optional.of(employee1));
        Mockito.when(employeeRepository.findByUsername(name2)).thenReturn(Optional.of(employee2));
        Assertions.assertEquals(employeeService.searchEmployee(name1),employee1);
        Assertions.assertEquals(employeeService.searchEmployee(name2),employee2);
    }
    @SneakyThrows
    @Test
    void searchEmployee_NotFound() {
        String name1 = "employeeNotFound1";
        String name2 = "employeeNotFound2";
        Mockito.when(employeeRepository.findByUsername(name1)).thenReturn(Optional.empty());
        Mockito.when(employeeRepository.findByUsername(name2)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,()->{employeeService.searchEmployee(name1);});
        Assertions.assertThrows(NotFoundException.class,()->{employeeService.searchEmployee(name2);});
    }
    @Test
    void getEmployeeList_true() {
        Employee employee1 = new Employee(1l,"name","qwe","name1",new Role(),true);
        Employee employee2 = new Employee(1l,"name","qwe","name2",new Role(),true);
        Employee employee3 = new Employee(1l,"name","qwe","name3",new Role(),true);
        Employee employee4 = new Employee(1l,"name","qwe","name4",new Role(),true);
        Mockito.when(employeeRepository.findAllByEnabled(true)).thenReturn(List.of(employee1,employee2,employee3,employee4));
        Assertions.assertEquals(employeeService.getEmployeeList(true),List.of(employee1,employee2,employee3,employee4));
    }
    @Test
    void getEmployeeList_false() {
        Employee employee1 = new Employee(1l,"name","qwe","name1",new Role(),false);
        Employee employee2 = new Employee(1l,"name","qwe","name2",new Role(),false);
        Employee employee3 = new Employee(1l,"name","qwe","name3",new Role(),false);
        Employee employee4 = new Employee(1l,"name","qwe","name4",new Role(),false);
        Mockito.when(employeeRepository.findAllByEnabled(false)).thenReturn(List.of(employee1,employee2,employee3,employee4));
        Assertions.assertEquals(employeeService.getEmployeeList(false),List.of(employee1,employee2,employee3,employee4));
    }

    @Test
    void saveEmployee() {
        Employee employee1 = new Employee(1l,"name","qwe","name1",new Role(),false);
        employeeService.saveEmployee(employee1);
        Mockito.verify(employeeRepository).save(employee1);
    }
    @SneakyThrows
    @Test
    void getEmployee_search() {
        Long id1 = 1l;
        Long id2 = 2l;
        Employee employee1 = new Employee(1l,"name","qwe","employeeFound1",new Role(),false);
        Employee employee2 = new Employee(2l,"name","qwe","employeeFound2",new Role(),false);
        Mockito.when(employeeRepository.findById(id1)).thenReturn(Optional.of(employee1));
        Mockito.when(employeeRepository.findById(id2)).thenReturn(Optional.of(employee2));
        Assertions.assertEquals(employeeService.getEmployee(id1),employee1);
        Assertions.assertEquals(employeeService.getEmployee(id2),employee2);
    }
    @SneakyThrows
    @Test
    void getEmployee_NotFound() {
        Long id1 = 1l;
        Long id2 = 2l;
        Mockito.when(employeeRepository.findById(id1)).thenReturn(Optional.empty());
        Mockito.when(employeeRepository.findById(id2)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,()->{employeeService.getEmployee(id1);});
        Assertions.assertThrows(NotFoundException.class,()->{employeeService.getEmployee(id2);});
    }
}