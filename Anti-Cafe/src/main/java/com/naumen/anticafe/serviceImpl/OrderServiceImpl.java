package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.EmployeeService;
import com.naumen.anticafe.service.GameZoneService;
import com.naumen.anticafe.service.GuestService;
import com.naumen.anticafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GuestService guestService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, GuestService guestService) {
        this.orderRepository = orderRepository;
        this.guestService = guestService;
    }

    @Override
    public Order getOrder(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        return optionalOrder.get();
    }
    public void checkPaymentOrder(Order order) throws NotFoundException {
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
    }

    @Override
    public void payment(Order order) throws NotFoundException {
        //проверяет оплату заказа, если заказ оплачен пробрасывает ошибку
        checkPaymentOrder(order);
        //посчитывает итоговую сумму
        calculateTotal(order);
        //имитирует оплату
        order.setPayment(true);
        orderRepository.save(order);
    }
    @Override
    public Order createOrder(Employee employee){
        Order order = new Order();
        order.setManager(employee);
        order.setDate(LocalDate.now());
        order.setPayment(false);
        order.setTaggedDelete(false);
        orderRepository.save(order);
        return order;
    }
    public List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId,
                                                       GameZone gameZone,
                                                       Boolean payment,
                                                       LocalDate reserveDate,
                                                       Employee employee,
                                                       boolean isTagged) throws NotFoundException {
        //выдает список заказов по указаным полям
        List<Order> orders = orderRepository
                .findAllByIdAndGameZoneAndPaymentAndReserveDateAndManagerAndTaggedDelete(
                        orderId,
                        gameZone,
                        payment,
                        reserveDate,
                        employee,
                        isTagged
                );
        return orders;
    }
    public void deleteOrder(Order order) throws NotFoundException {
        checkPaymentOrder(order);
        List<Guest> guestList = guestService.getGuestListByOrder(order);
        for (Guest g:guestList){
            guestService.deleteGuestWithCart(g);
        }
        orderRepository.delete(order);

    }
    public void save(Order order){
        orderRepository.save(order);
    }
    public void calculateTotal(Order order){
        //список всех гостей
        List<Guest> guestList = guestService.getGuestListByOrder(order);
        //список всех товаров гостей
        List<GuestCart> guestCartList = new ArrayList<>();
        //помещает все товары в список
        for (Guest g : guestList) {
            guestCartList.addAll(guestService.getProductGuest(g));
        }
        //Высчитывание итоговой цены
        int total = 0;
        for (GuestCart gc : guestCartList) {
            Product product = gc.getProduct();
            total += (product.getPrice() * gc.getQuantity());
        }
        order.setTotal(total);
    }
    public List<Order> getOrderByGameZoneAndReserveDate(GameZone gameZone,LocalDate localDate){
        return orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, localDate);
    }
}
