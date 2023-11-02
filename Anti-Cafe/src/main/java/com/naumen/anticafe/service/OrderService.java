package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(Employee employee);
    void payment(Order order) throws NotFoundException ;
    Order getOrder(Long orderId) throws NotFoundException;
    void calculateTotal(Order order);
    void save(Order order);
    List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone, LocalDate localDate);
    void deleteOrder(Order order) throws NotFoundException;
    List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                GameZone gameZone,
                                                Boolean payment,
                                                LocalDate reserveDate,
                                                Employee employee,
                                                boolean isTagged) throws NotFoundException ;
    void checkPaymentOrder(Order order) throws NotFoundException;
}
