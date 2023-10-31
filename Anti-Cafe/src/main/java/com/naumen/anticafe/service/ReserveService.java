package com.naumen.anticafe.service;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

import java.util.List;

public interface ReserveService {
    void deleteReserve(Order order) throws NotFoundException;
    void setReserve(Order order,
                    String dayOfMount,
                    Long gameZoneId,
                    int freeTime,
                    int maxHour,
                    int hour) throws NotFoundException;
    List<String> getAllDayOfReserve();
    Integer[][] getFreeTimesAndMaxHourReserve(GameZone gameZone, String dayMonth) throws NotFoundException;
}
