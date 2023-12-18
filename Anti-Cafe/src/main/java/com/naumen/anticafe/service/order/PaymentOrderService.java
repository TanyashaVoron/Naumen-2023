package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Order;

public interface PaymentOrderService {
    void payment(Order order);
}
