package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;

import java.util.List;

public interface GuestService {
    void addGuest(Order order) throws NotFoundException;
    void deleteGuest(Guest guest)throws GuestsHaveGoodsException;
    List<Guest> getGuestListByOrder(Order order);
    List<GuestCart> getGuestCartListByGuest(List<Guest> guestList);
    List<GuestCart> getProductGuest(Guest guest);
    Guest getGuest(Long guestId, Order order) throws NotFoundException;
}
