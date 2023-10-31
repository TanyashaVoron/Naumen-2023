package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(Employee employee);
    void payment(Order order) throws NotFoundException ;
    Order getOrder(Long orderId) throws NotFoundException;
    void calculateTotal(Order order);
    void save(Order order);
    List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone, LocalDate localDate);
    List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                GameZone gameZone,
                                                Boolean payment,
                                                LocalDate reserveDate,
                                                Employee employee) throws NotFoundException ;
    void checkPaymentOrder(Order order) throws NotFoundException;
}
