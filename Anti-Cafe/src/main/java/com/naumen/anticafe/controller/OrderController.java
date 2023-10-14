package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.*;
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
    private EmployeeRepository employeeRepository;
    private GuestRepository guestRepository;
    private GuestCartRepository guestCartRepository;
    private GameZoneRepository gameZoneRepository;

    public OrderController(OrderRepository orderRepository,
                           EmployeeRepository employeeRepository,
                           GuestRepository guestRepository,
                           GuestCartRepository guestCartRepository,
                           GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.guestRepository = guestRepository;
        this.guestCartRepository = guestCartRepository;
        this.gameZoneRepository = gameZoneRepository;
    }
    @GetMapping("/{id}")
    public String orderShow(@PathVariable("id")Long id, Model model){

        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isEmpty()) return "orderNotFound";
        Order order = optionalOrder.get();
        List<Guest> guestList = guestRepository.findAllByOrder(order);
        List<GuestCart> guestCartList = new ArrayList<>();
        for(Guest g : guestList){
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        GameZone gameZone = order.getGameZone();
        Optional<GameZone> optionalGameZone = Optional.of(gameZone);
        Employee employee = order.getManager();
        model.addAttribute("total",order.getTotal());
        model.addAttribute("gameZone",optionalGameZone);
        model.addAttribute("products",guestCartList);
        model.addAttribute("guests",guestList);
        if(employee!=null) model.addAttribute("manager", employee);
        return "order";
    }
    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee){
        Order order = new Order();
        order.setManager(employee);
        order.setDate(new Date(System.currentTimeMillis()));
        Optional<GameZone> optionalGameZone = gameZoneRepository.findById(Long.valueOf(5));
        GameZone gameZone = optionalGameZone.get();
        order.setGameZone(gameZone);
        order = orderRepository.save(order);
        return "redirect:/order/"+order.getId();
    }
}
