package com.naumen.anticafe.service.guest;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

public interface GuestService {
    long deleteGuest(Guest guest);
    void addGuest(Order order);
    void deleteGuestCascade(Guest guest);
    Guest getGuest(Long guestId, Order order) throws NotFoundException;
}
