package com.naumen.anticafe.service.order.reserve;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;

import java.time.LocalDate;
import java.util.List;

public interface ReserveService {
    boolean checkReserve(LocalDate date, int freeHour, int hour, GameZone gameZone);

    void deleteReserve(Long orderId);

    void setReserve(Order order,
                    LocalDate reserveDay,
                    GameZone gameZone,
                    int freeTime,
                    int hour);

    List<String> getAllDayOfReserve();

    int[][] getFreeTimesAndMaxHourReserve(GameZone gameZone, LocalDate dayReserve);
}
