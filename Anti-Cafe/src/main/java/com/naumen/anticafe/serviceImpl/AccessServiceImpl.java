package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NoAccessToOperation;
import com.naumen.anticafe.service.AccessService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccessServiceImpl implements AccessService {
    @Override
    public boolean isAccessOrder(Employee employeeNow, Order order){
        //получает роль персонажа
        Role role = employeeNow.getRole();
        //переберет роли и если у него роль менеджера то проверяет являться ли он владельцем заказа
        if (role.getRole().equals("ROLE_MANAGER")) {
            if (!order.getManager().equals(employeeNow)) {
                return false;
            }
        }

        return true;
    }
}
