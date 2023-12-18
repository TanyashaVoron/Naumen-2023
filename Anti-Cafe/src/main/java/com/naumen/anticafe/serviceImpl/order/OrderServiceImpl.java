package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Создает пустой заказ
     */
    @Override
    @Transactional
    public Order createOrder(Employee employee) {
        Order order = new Order();
        order.setManager(employee);
        order.setDate(LocalDate.now());
        order.setPayment(false);
        order.setTaggedDelete(false);
        orderRepository.save(order);
        return order;
    }


    /**
     * Получает заказ по ид
     */
    @Override
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) throws NotFoundException {
        //если не нашел выбрасывает ошибку
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        return optionalOrder.get();
    }

    /**
     * Удаляет заказ каскадом
     */
    @Override
    @Transactional
    public void deleteOrderCascade(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
