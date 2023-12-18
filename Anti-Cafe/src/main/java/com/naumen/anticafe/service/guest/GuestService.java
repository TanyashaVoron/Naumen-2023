package com.naumen.anticafe.service.guest;

import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;

public interface GuestService {
    Integer deleteGuestByNotExistCart(GuestId guestId);

    void addGuest(Order order);
}
