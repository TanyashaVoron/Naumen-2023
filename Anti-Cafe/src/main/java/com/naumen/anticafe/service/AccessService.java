package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NoAccessToOperation;

public interface AccessService {
    void isAccessOrder(Employee employeeNow, Order order) throws NoAccessToOperation;
}
