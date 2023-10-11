package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {
    private OrderRepository orderRepository;
    private GameZoneRepository gameZoneRepository;

    public SearchController(OrderRepository orderRepository, GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    @GetMapping()
    public String searchShow(Model model,
                             @RequestParam(value = "order",required = false) Long order,
                             @RequestParam(value = "gamezone", required = false) Long gameZone){
        //создание пустого списка
        List<Order> orders = new ArrayList<>();
        //проверка на пустые параметры и поиск без параметров
        if(order==null&&gameZone==null) orders = orderRepository.findAll();
        //выполняет поиск по ордеру
        else if(gameZone==null) orders = orderRepository.findAllById(order);
        //выполняет поиск по гейм зоне
        else if (order==null) {
            Optional<GameZone> gz = gameZoneRepository.findById(gameZone);
            //проверяет поиск на нуль и после чего ищет по игровой зоне
            if(!gz.isEmpty()) orders = orderRepository.findAllByGameZone(gz.get());
        }
        //выполняет поиск по двум полям
        else  {
            Optional<GameZone> gz = gameZoneRepository.findById(gameZone);
            //проверяет поиск на нуль и после чего ищет по всем параметрам
            if(!gz.isEmpty()) orders =orderRepository.findByIdAndGameZone(order,gz.get());
        }
        //добавляет в модель список найденных заказов если они есть
        model.addAttribute("orders",orders);
        return "search";
    }
}
