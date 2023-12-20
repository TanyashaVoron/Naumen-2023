package com.naumen.anticafe.serviceImpl.guestCart;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GuestCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GuestCartServiceImplTest {
    @InjectMocks
    private GuestCartServiceImpl guestCartService;
    @Mock
    private GuestCartRepository guestCartRepository;

    @Test
    void getGuestCartListByOrder() {
        Order order = new Order();
        List<GuestCart> guestCartList = new ArrayList<>();
        List<Guest> guests = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Guest guest = new Guest();
            guests.add(guest);
            GuestCart guestCart = new GuestCart();
            guestCart.setId((long) i);
            guestCartList.add(guestCart);
            guest.setCart(List.of(guestCart));
        }
        order.setGuests(guests);
        for (int i = 0; i < guests.size(); i++) {
            Mockito.when(guestCartRepository.findAllByGuest(guests.get(i))).thenReturn(guests.get(i).getCart());
        }
        Assertions.assertEquals(guestCartService.getGuestCartListByOrder(order),guestCartList);
    }
}