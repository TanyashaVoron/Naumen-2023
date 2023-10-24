package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping()
    public String searchShow(Model model,
                             @RequestParam(value = "orderId",required = false) Long orderId,
                             @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                             @RequestParam(value = "payment",required = false) Boolean payment,
                             @RequestParam(value = "date",required = false) LocalDate reserveDate,
                             @RequestParam(value = "employee",required = false) Long employeeId,
                             @AuthenticationPrincipal Employee employee){
        Iterable<Employee> employeeIterable = searchService.getEmployees();
        List<Order> orders = searchService.getOrderByIdOrGameZoneOrPayment(orderId,gameZoneId,payment,reserveDate,employeeId);
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        //добавляет в модель список найденных заказов если они есть
        model.addAttribute("orders",orders);
        model.addAttribute("employees",employeeIterable);
        return "search";
    }
}
