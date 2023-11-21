package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Order;

public interface CalculationTotalService {
    void calculateTotal(Order order);
}
