package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(Employee employee);
    List<Order> getOrderMarkDeletion(LocalDate localDate);
    Order getOrder(Long orderId) throws NotFoundException;
    void save(Order order);
    void deleteOrderCascade(Order order) throws NotFoundException;
}
