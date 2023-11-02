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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orderManagement")
public class OrderManagementController {
    private final OrderService orderService;
    private final EmployeeService employeeService;
    private final GameZoneService gameZoneService;

    @Autowired
    public OrderManagementController(OrderService orderService,
                                     EmployeeService employeeService,
                                     GameZoneService gameZoneService) {
        this.orderService = orderService;
        this.employeeService = employeeService;
        this.gameZoneService = gameZoneService;
    }

    @GetMapping()
    public String showOrderManagement(Model model,
                                      @RequestParam(value = "orderId", required = false) Long orderId,
                                      @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                                      @RequestParam(value = "payment", required = false) Boolean payment,
                                      @RequestParam(value = "date", required = false) LocalDate reserveDate,
                                      @RequestParam(value = "employee", required = false) Employee employeeSearch,
                                      @AuthenticationPrincipal Employee employee) {
        try {
            List<Employee> employeeList = employeeService.getEmployeeList(true);
            GameZone gameZone = null;
            if (gameZoneId != null) gameZone = gameZoneService.getGameZone(gameZoneId);
            List<Order> orders = orderService
                    .getOrderByIdOrGameZoneOrPayment(
                            orderId,
                            gameZone,
                            payment,
                            reserveDate,
                            employeeSearch,
                            true);
            model.addAttribute("user", Optional.ofNullable(employee));
            model.addAttribute("orders", orders);
            model.addAttribute("employees", employeeList);
            return "orderManagement";
        } catch (NotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/delete")
    public String deleteOrder(@ModelAttribute("orderId") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrder(orderId);
            orderService.deleteOrder(order);
            return "redirect:/orderManagement";
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/restore")
    public String restoreOrder(@ModelAttribute("orderId") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrder(orderId);
            order.setTaggedDelete(false);
            orderService.save(order);
            return "redirect:/orderManagement";
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }
}
