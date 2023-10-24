package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import org.springframework.ui.Model;

import java.util.Optional;

public interface OrderService {
    String createOrder(Employee employee);
    String reserveDelete(Long orderId);
    String payment(Long orderId);
    String addGuest(Long orderId);
    String orderShow(Long orderId,Employee employee, Model model);
    public String reserveShow(Long orderId, Long gameZoneId, String dayMonth, Employee employee, Model model);
    String addReserve(Long orderId, String dayOfMount, Long gameZoneId, int freeTime, int maxHour, String hours);
    String deleteGuest(Long orderId, Long guestId);
}
