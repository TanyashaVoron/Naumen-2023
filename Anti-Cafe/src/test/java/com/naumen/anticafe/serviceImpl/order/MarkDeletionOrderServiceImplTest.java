package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class MarkDeletionOrderServiceImplTest {
    @InjectMocks
    private MarkDeletionOrderServiceImpl markDeletionOrderService;
    @Mock
    private OrderRepository orderRepository;

    @Test
    void markForDeletion_true() {
        Long orderId = 1L;
        LocalDate ld = LocalDate.now();
        markDeletionOrderService.markForDeletion(orderId,true,ld);
        Mockito.verify(orderRepository, Mockito.times(1)).setTaggedDeleteAndTimerTaggedDelete(orderId,true,ld);
    }

    @Test
    void markForDeletion_false() {
        Long orderId = 1L;
        LocalDate ld = LocalDate.now();
        markDeletionOrderService.markForDeletion(orderId,false,ld);
        Mockito.verify(orderRepository, Mockito.times(1)).setTaggedDeleteAndTimerTaggedDelete(orderId,false,ld);
    }

    @Test
    void getOrderMarkDeletion() {
        LocalDate ld = LocalDate.now();
        markDeletionOrderService.getOrderMarkDeletion(ld);
        Mockito.verify(orderRepository, Mockito.times(1)).findAllByTimerTaggedDelete(ld);
    }
}