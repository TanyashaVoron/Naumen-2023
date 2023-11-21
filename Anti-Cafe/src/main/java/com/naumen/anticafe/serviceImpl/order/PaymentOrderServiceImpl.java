package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.order.CalculationTotalService;
import com.naumen.anticafe.service.order.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {

    private final CalculationTotalService calculateTotalService;

    @Autowired
    public PaymentOrderServiceImpl(CalculationTotalService calculateTotalService) {
        this.calculateTotalService = calculateTotalService;
    }

    @Override
    public void checkPaymentOrder(Order order) throws NotFoundException {
        if (order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
    }

    @Override
    public void payment(Order order) throws NotFoundException {
        //проверяет оплату заказа, если заказ оплачен пробрасывает ошибку
        checkPaymentOrder(order);
        //посчитывает итоговую сумму
        calculateTotalService.calculateTotal(order);
        //имитирует оплату
        order.setPayment(true);
    }
}
