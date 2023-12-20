package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class AccessServiceImplTest {

    @Test
    void isAccessOrder_True_manager() {
        AccessServiceImpl accessService = new AccessServiceImpl();
        Role role = new Role(1,"ROLE_MANAGER");
        Employee employee1 = new Employee();
        employee1.setRole(role);
        employee1.setName("qwe");
        Order order = new Order();
        order.setManager(employee1);
        Assertions.assertTrue(accessService.isAccessOrder(employee1,order));
    }
    @Test
    void isAccessOrder_True_admin() {
        AccessServiceImpl accessService = new AccessServiceImpl();
        Role role = new Role(1,"ROLE_ADMIN");
        Employee employee1 = new Employee();
        employee1.setRole(role);
        employee1.setName("qwe");
        Role role1 = new Role(2,"ROLE_MANAGER");
        Employee employee2 = new Employee();
        employee2.setRole(role1);
        employee2.setName("qwe");
        Order order = new Order();
        order.setManager(employee2);
        Assertions.assertTrue(accessService.isAccessOrder(employee1,order));
    }
    @Test
    void isAccessOrder_False() {
        AccessServiceImpl accessService = new AccessServiceImpl();
        Role role = new Role(1,"ROLE_ADMIN");
        Employee employee1 = new Employee();
        employee1.setRole(role);
        employee1.setName("qwe");
        Role role1 = new Role(2,"ROLE_MANAGER");
        Employee employee2 = new Employee();
        employee2.setRole(role1);
        employee2.setName("qwe");
        Order order = new Order();
        order.setManager(employee2);
        Assertions.assertTrue(accessService.isAccessOrder(employee2,order));
    }
}