package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.service.AccessService;
import org.springframework.stereotype.Service;

@Service
public class AccessServiceImpl implements AccessService {
    /**
     * Проверяет доступ сотрудника к заказу
     */
    @Override
    public boolean isAccessOrder(Employee employeeNow, Order order) {
        //получает роль сотрудника
        Role role = employeeNow.getRole();
        //проверяет являеться ли сотрудник простым официнтом
        if (role.getRole().equals("ROLE_MANAGER")) {
            //проверяет владельца и текущего сотрудник
            return order.getManager().equals(employeeNow);
        }
        return true;
    }
}
