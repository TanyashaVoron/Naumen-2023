package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.exception.NotFoundException;

public interface OrderService {
    Order createOrder(Employee employee);

    Order getOrder(Long orderId) throws NotFoundException;

    void deleteOrderCascade(Long orderId);
}
