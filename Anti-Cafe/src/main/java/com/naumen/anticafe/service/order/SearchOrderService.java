package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface SearchOrderService {
    List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone, LocalDate localDate);
    List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                GameZone gameZone,
                                                Boolean payment,
                                                LocalDate reserveDate,
                                                Employee employee,
                                                boolean isTagged) throws NotFoundException;
}
