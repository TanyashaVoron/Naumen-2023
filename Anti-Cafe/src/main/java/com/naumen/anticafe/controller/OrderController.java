package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.GuestCartRepository;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.repository.OrderRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    private OrderRepository orderRepository;
    private GuestRepository guestRepository;
    private GuestCartRepository guestCartRepository;
    private GameZoneRepository gameZoneRepository;

    public OrderController(OrderRepository orderRepository,
                           GuestRepository guestRepository,
                           GuestCartRepository guestCartRepository,
                           GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.guestRepository = guestRepository;
        this.guestCartRepository = guestCartRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    @GetMapping("/{id}")
    public String orderShow(@PathVariable("id") Long id, Model model) {
        //находит заказ по ид
        Optional<Order> optionalOrder = orderRepository.findById(id);
        //если заказ null то вызывается страничка ошибки
        if (optionalOrder.isEmpty()) return "orderNotFound";
        //если заказ найден присваивает переменной
        Order order = optionalOrder.get();
        //находит всех гостей этого заказа и помещает их в guestList
        List<Guest> guestList = guestRepository.findAllByOrder(order);
        //создает список товаров сделанных гостями этого заказа
        List<GuestCart> guestCartList = new ArrayList<>();
        //помещает все товары в список
        for (Guest g : guestList) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        //создает переменную игровой зоный заказы
        Optional<GameZone> gameZone = Optional.ofNullable(order.getGameZone());
        //создает переменную менеджера
        Optional<Employee> employee = Optional.ofNullable(order.getManager());
        //передает все в модель
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal());
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        model.addAttribute("gameZone", gameZone);
        model.addAttribute("manager", employee);
        return "order";
    }

    @PostMapping("/{id}/add_guest")
    public String addGuest(@PathVariable("id") Long id) {
        //Создает гостя
        Guest guest = new Guest();
        //получает заказ по ид
        Order order = orderRepository.findById(id).get();
        //присваивает гостя к заказу
        guest.setOrder(order);
        //получает и инкрементирует номер текущего гостя
        long count = guestRepository.countByOrder(order);
        count++;
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
        //переадресовывает на заказ
        return "redirect:/order/" + order.getId();
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        //создает заказ
        Order order = new Order();
        //устанавливает менеджера, который создал заказ
        order.setManager(employee);
        //устанавливает дату создания заказа
        order.setDate(new Date(System.currentTimeMillis()));
        //сохраняет в бд заказ
        order = orderRepository.save(order);
        //переадрисовывает на заказ
        return "redirect:/order/" + order.getId();
    }
}
