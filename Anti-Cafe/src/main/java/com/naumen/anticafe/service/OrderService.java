package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import org.springframework.ui.Model;

import java.util.List;

public interface OrderService {
    Order createOrder(Employee employee);
    Order deleteReserve(Long orderId) throws NotFoundException;
    Order payment(Long orderId) throws NotFoundException;
    Order addGuest(Long orderId) throws NotFoundException;
    Order getOrder(Long orderId) throws NotFoundException;
    Order deleteGuest(Long orderId, Long guestId) throws NotFoundException, GuestsHaveGoodsException;
    List<Guest> getGuestListByOrder(Order order);
    List<GuestCart> getGuestCartListByGuest(List<Guest> guestList);
    Order setReserve(Long orderId, String dayOfMount, Long gameZoneId, int freeTime, int maxHour, int hour) throws NotFoundException;
    List<GameZone> getGameZoneList();
    List<String> getAllDayOfReserve();
    Integer[][] getFreeTimesAndMaxHourReserve(Long gameZone, String dayMonth) throws NotFoundException;
}
