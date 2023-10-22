package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.OrderRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {
    private final OrderRepository orderRepository;
    private final GameZoneRepository gameZoneRepository;

    public SearchController(OrderRepository orderRepository, GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    @GetMapping()
    public String searchShow(Model model,
                             @RequestParam(value = "orderId",required = false) Long order,
                             @RequestParam(value = "gameZoneId", required = false) Long gameZone,
                             @AuthenticationPrincipal Employee employee){
        //создание пустого списка
        List<Order> orders = new ArrayList<>();
        //проверка на пустые параметры и поиск без параметров
        if(order==null&&gameZone==null) orders = orderRepository.findAllByOrderById();
        //выполняет поиск по ордеру
        else if(gameZone==null) orders = orderRepository.findAllById(order);
        //выполняет поиск по гейм зоне
        else if (order==null) {
            Optional<GameZone> gz = gameZoneRepository.findById(gameZone);
            //проверяет поиск на нуль и после чего ищет по игровой зоне
            if(!gz.isEmpty()) orders = orderRepository.findAllByGameZoneOrderById(gz.get());
        }
        //выполняет поиск по двум полям
        else  {
            Optional<GameZone> gz = gameZoneRepository.findById(gameZone);
            //проверяет поиск на нуль и после чего ищет по всем параметрам
            if(!gz.isEmpty()) orders =orderRepository.findByIdAndGameZoneOrderById(order,gz.get());
        }
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        //добавляет в модель список найденных заказов если они есть
        model.addAttribute("orders",orders);
        return "search";
    }
}
