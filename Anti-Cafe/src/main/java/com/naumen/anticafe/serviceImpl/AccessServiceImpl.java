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
    public void isAccessOrder(Employee employeeNow, Order order) throws NoAccessToOperation {
        //получает роль персонажа
        Set<Role> roles = employeeNow.getRole();
        //переберет роли и если у него роль менеджера то проверяет являться ли он владельцем заказа
        for (Role r : roles) {
            if (r.getRole().equals("ROLE_MANAGER")) {
                if (!order.getManager().equals(employeeNow)) {
                    throw new NoAccessToOperation("У сотрудника нет доступа к этой операции обратитесь к Администратору либо к главному Менеджеру", employeeNow.getName(),order.getManager().getName());
                }
            }
        }
    }
}
