package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.EmployeeService;
import com.naumen.anticafe.service.GameZoneService;
import com.naumen.anticafe.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final OrderService orderService;
    private final EmployeeService employeeService;
    private final GameZoneService gameZoneService;
    @Autowired
    public SearchController(OrderService orderService, EmployeeService employeeService, GameZoneService gameZoneService) {
        this.orderService = orderService;
        this.employeeService = employeeService;
        this.gameZoneService = gameZoneService;
    }

    @GetMapping()
    public String searchShow(Model model,
                             @RequestParam(value = "orderId",required = false) Long orderId,
                             @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                             @RequestParam(value = "payment",required = false) Boolean payment,
                             @RequestParam(value = "date",required = false) LocalDate reserveDate,
                             @RequestParam(value = "employee",required = false) Employee employeeSearch,
                             @AuthenticationPrincipal Employee employee) {
        List<Employee> employeeList = employeeService.getEmployeeList(true);
        List<Order> orders;
        try {
            GameZone gameZone = null;
            if(gameZoneId != null) gameZone = gameZoneService.getGameZone(gameZoneId);
            orders = orderService.getOrderByIdOrGameZoneOrPayment(orderId,gameZone,payment,reserveDate,employeeSearch,false);
        } catch (NotFoundException e) {
            model.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user",optionalEmployee);
        //добавляет в модель список найденных заказов если они есть
        model.addAttribute("orders",orders);
        model.addAttribute("employees",employeeList);
        return "search";
    }
}
