package com.naumen.anticafe.serviceImpl.order;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.SearchOrderService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, localDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                       GameZone gameZone,
                                                       Boolean payment,
                                                       LocalDate reserveDate,
                                                       Employee employee,
                                                       boolean isTagged){
        //выдает список заказов по указанным полям
        return orderRepository
                .findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(
                        orderId,
                        gameZone,
                        payment,
                        reserveDate,
                        employee,
                        isTagged
                );
    }
}
