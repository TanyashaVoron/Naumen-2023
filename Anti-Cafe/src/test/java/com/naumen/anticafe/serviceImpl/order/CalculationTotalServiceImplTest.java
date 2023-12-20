package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.domain.Product;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import com.naumen.anticafe.service.order.CalculationTotalService;
import org.hibernate.annotations.Immutable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CalculationTotalServiceImplTest {
    @InjectMocks
    private CalculationTotalServiceImpl calculationTotalService;
    @Mock
    private GuestCartService guestCartService;
    @Test
    void calculateTotal() {
        Order order = new Order();
        List<GuestCart> guestCartList = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < 10; i++) {
            GuestCart guestCart =new GuestCart();
            Product product = new Product();
            product.setPrice(i);
            guestCart.setQuantity(i*13);
            guestCart.setProduct(product);
            total += (i*i*13);
            guestCartList.add(guestCart);
        }
        Mockito.when(guestCartService.getGuestCartListByOrder(order)).thenReturn(guestCartList);
        calculationTotalService.calculateTotal(order);
        assertEquals(order.getTotal(), total);
    }
}