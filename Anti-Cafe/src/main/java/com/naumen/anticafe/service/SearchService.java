package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;

import java.time.LocalDate;
import java.util.List;

public interface SearchService {
    public List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId, Long gameZoneId, Boolean payment, LocalDate reserveDate, Long employeeId);
    public Iterable<Employee> getEmployees();
}
