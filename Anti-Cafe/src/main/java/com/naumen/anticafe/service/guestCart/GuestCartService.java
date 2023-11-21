package com.naumen.anticafe.service.guestCart;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;

import java.util.List;

public interface GuestCartService {
    List<GuestCart> getGuestCartListByOrder(Order order);
    List<GuestCart> getProductGuest(Guest guest);
    Long countByGuest(Guest guest);
}
