package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Order;

import java.time.LocalDate;
import java.util.List;

public interface MarkDeletionOrderService {
    void markForDeletion(Long id, boolean taggedDelete, LocalDate localDate);

    List<Order> getOrderMarkDeletion(LocalDate localDate);
}
