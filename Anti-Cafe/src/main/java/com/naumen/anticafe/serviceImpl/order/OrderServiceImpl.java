package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentOrderServiceImpl paymentOrderService;
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, PaymentOrderServiceImpl paymentOrderService) {
        this.orderRepository = orderRepository;
        this.paymentOrderService = paymentOrderService;
    }

    @Override
    public Order createOrder(Employee employee) {
        Order order = new Order();
        order.setManager(employee);
        order.setDate(LocalDate.now());
        order.setPayment(false);
        order.setTaggedDelete(false);
        orderRepository.save(order);
        return order;
    }

    @Override
    public List<Order> getOrderMarkDeletion(LocalDate localDate) {
        return orderRepository.findAllByTimerTaggedDelete(localDate);
    }

    @Override
    public Order getOrder(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        return optionalOrder.get();
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }
    @Override
    public void deleteOrderCascade(Order order) throws NotFoundException {
        paymentOrderService.checkPaymentOrder(order);
        orderRepository.delete(order);

    }
}
