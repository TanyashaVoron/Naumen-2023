package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;

public interface AccessService {
    boolean isAccessOrder(Employee employeeNow, Order order);
}
