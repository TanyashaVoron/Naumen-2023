package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.service.order.CalculationTotalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentOrderServiceImplTest {
    @InjectMocks
    private PaymentOrderServiceImpl paymentOrderService;
    @Mock
    private CalculationTotalService calculateTotalService;

    @Test
    void payment() {
        Order order = new Order();
        paymentOrderService.payment(order);
        Mockito.verify(calculateTotalService,Mockito.times(1)).calculateTotal(order);
        Assertions.assertTrue(order.getPayment());
    }
}