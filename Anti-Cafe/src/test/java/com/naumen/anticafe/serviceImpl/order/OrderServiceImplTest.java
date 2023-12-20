package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.OrderService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;


    @Test
    void createOrder() {
    }

    @SneakyThrows
    @Test
    void getOrder_found_andException() {
        Order order = new Order();
        order.setId(1L);
        Long id = 1L;
        Long exceptionId = 2L;
        //найден
        Mockito.when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        Assertions.assertEquals(orderService.getOrder(id), order);
        Mockito.verify(orderRepository, Mockito.times(1)).findById(id);
        //ненайден
        Mockito.when(orderRepository.findById(exceptionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> orderService.getOrder(exceptionId));
        Mockito.verify(orderRepository, Mockito.times(1)).findById(exceptionId);
    }

    @Test
    void deleteOrderCascade() {
        Long orderId = 1L;
        orderService.deleteOrderCascade(orderId);
        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(orderId);
    }
}