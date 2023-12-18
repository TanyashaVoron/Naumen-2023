package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Product;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import com.naumen.anticafe.service.order.CalculationTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CalculationTotalServiceImpl implements CalculationTotalService {
    private final GuestCartService guestCartService;

    @Autowired
    public CalculationTotalServiceImpl(GuestCartService guestCartService) {
        this.guestCartService = guestCartService;
    }

    /**
     * Высчитывает итоговую сумму
     */
    @Transactional
    public void calculateTotal(Order order) {
        //список всех гостей
        List<GuestCart> guestCartList = guestCartService.getGuestCartListByOrder(order);
        //Высчитывание итоговой цены
        int total = 0;
        for (GuestCart gc : guestCartList) {
            Product product = gc.getProduct();
            total += (product.getPrice() * gc.getQuantity());
        }
        order.setTotal(total);
    }
}
