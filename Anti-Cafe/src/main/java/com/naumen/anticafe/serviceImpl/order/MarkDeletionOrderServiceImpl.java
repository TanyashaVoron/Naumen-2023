package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.MarkDeletionOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarkDeletionOrderServiceImpl implements MarkDeletionOrderService {
    private final OrderRepository orderRepository;

    public MarkDeletionOrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * меняет маркировку удаления в заказе
     */
    @Override
    @Transactional
    public void markForDeletion(Long id, boolean taggedDelete, LocalDate localDate) {
        orderRepository.setTaggedDeleteAndTimerTaggedDelete(id, taggedDelete, localDate);
    }

    /**
     * выискивает заказы помеченые по указанной дате
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderMarkDeletion(LocalDate localDate) {
        return orderRepository.findAllByTimerTaggedDelete(localDate);
    }
}
