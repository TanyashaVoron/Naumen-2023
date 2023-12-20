package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SearchOrderServiceImplTest {
    @InjectMocks
    private SearchOrderServiceImpl searchOrderService;
    @Mock
    private OrderRepository orderRepository;

    @Test
    void getOrderByGameZoneAndReserveDate() {
        Order o1 = new Order();
        o1.setId(1L);
        Order o2 = new Order();
        o2.setId(2L);
        Order o3 = new Order();
        o3.setId(3L);
        GameZone gameZone = new GameZone();
        LocalDate ld = LocalDate.now();
        List<Order> orderList1 = Arrays.asList(o1,o2,o3);
        List<Order> orderList2 = Arrays.asList(o1,o2,o3);
        Mockito.when(orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, ld)).thenReturn(orderList1);
        Assertions.assertEquals(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone,ld),orderList2);
        Mockito.verify(orderRepository,Mockito.times(1)).findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone,ld);
    }

    @Test
    void getOrderByIdOrGameZoneOrPayment() {
        Order o1 = new Order();
        o1.setId(1L);
        Order o2 = new Order();
        o2.setId(2L);
        Order o3 = new Order();
        o3.setId(3L);
        Long orderId = 1L;
        Long gameZoneId = 1L;
        Boolean payment = true;
        LocalDate reserveDate = LocalDate.now();
        Long employeeId = 1L;
        boolean isTagged = true;
        Pageable pageable = PageRequest.of(2,2);
        Page<Order> pageE1 = new PageImpl<>(List.of(o1,o2,o3));
        Page<Order> pageE2 = new PageImpl<>(List.of(o1,o2,o3));
        Mockito.when(orderRepository.findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(
                orderId,
                gameZoneId,
                payment,
                reserveDate,
                employeeId,
                isTagged,
                pageable
        )).thenReturn(pageE1);
        Assertions.assertEquals(searchOrderService.getOrderByIdOrGameZoneOrPayment(                orderId,
                gameZoneId,
                payment,
                reserveDate,
                employeeId,
                isTagged,
                pageable),pageE2);
        Mockito.verify(orderRepository,Mockito.times(1))
                .findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(
                orderId,
                gameZoneId,
                payment,
                reserveDate,
                employeeId,
                isTagged,
                pageable
        );
    }
}