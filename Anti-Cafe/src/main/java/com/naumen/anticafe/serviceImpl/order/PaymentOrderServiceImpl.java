package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.service.order.CalculationTotalService;
import com.naumen.anticafe.service.order.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {

    private final CalculationTotalService calculateTotalService;

    @Autowired
    public PaymentOrderServiceImpl(CalculationTotalService calculateTotalService) {
        this.calculateTotalService = calculateTotalService;
    }

    /**
     * посчитывает итоговую сумму
     */
    @Override
    @Transactional
    public void payment(Order order) {
        calculateTotalService.calculateTotal(order);
        //имитирует оплату
        order.setPayment(true);
    }
}
