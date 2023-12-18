package com.naumen.anticafe.service.order;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SearchOrderService {
    List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone, LocalDate localDate);

    Page<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                Long gameZoneId,
                                                Boolean payment,
                                                LocalDate reserveDate,
                                                Long employeeId,
                                                boolean isTagged,
                                                Pageable pageable);
}
