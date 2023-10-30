package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

import java.util.List;

public interface ReserveService {
    Order deleteReserve(Long orderId) throws NotFoundException;
    Order setReserve(Long orderId, String dayOfMount, Long gameZoneId, int freeTime, int maxHour, int hour) throws NotFoundException;
    List<String> getAllDayOfReserve();
    Integer[][] getFreeTimesAndMaxHourReserve(Long gameZone, String dayMonth) throws NotFoundException;
}
