package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

public interface PaymentOrderService {
    void payment(Order order) throws NotFoundException;
    void checkPaymentOrder(Order order) throws NotFoundException;
}
