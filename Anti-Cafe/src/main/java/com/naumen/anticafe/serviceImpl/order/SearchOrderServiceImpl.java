package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.SearchOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchOrderServiceImpl implements SearchOrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public SearchOrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone, LocalDate localDate) {
        /* Находит заказы по игровой зоне и по дате резерва*/
        return orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, localDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                       Long gameZoneId,
                                                       Boolean payment,
                                                       LocalDate reserveDate,
                                                       Long employeeId,
                                                       boolean isTagged,
                                                       Pageable pageable) {
        /*выдает список заказов по указанным полям*/
        return orderRepository
                .findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(
                        orderId,
                        gameZoneId,
                        payment,
                        reserveDate,
                        employeeId,
                        isTagged,
                        pageable
                );
    }
}
