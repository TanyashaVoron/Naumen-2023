package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {

        this.orderService = orderService;
    }

    @GetMapping("/{id}/reserve")
    public String reserveShow(@PathVariable("id") Long orderId,
                              @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                              @RequestParam(value = "dayMonth", required = false) String dayMonth,
                              @AuthenticationPrincipal Employee employee,
                              Model model) {
        return orderService.reserveShow(orderId,gameZoneId,dayMonth,employee,model);
    }
    @PostMapping("/{id}/reserve/Add")
    public String addReserve(@PathVariable("id") Long orderId,
                             @ModelAttribute(value = "dayOfMount") String dayOfMount,
                             @ModelAttribute(value = "gameZoneId") Long gameZoneId,
                             @ModelAttribute(value = "freeTime") int freeTime,
                             @ModelAttribute(value = "maxHour") int maxHour,
                             @ModelAttribute(value = "hour") String hours) {
        return orderService.addReserve(orderId,dayOfMount,gameZoneId,freeTime,maxHour,hours);
    }

    @GetMapping("/{id}")
    public String orderShow(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employeeAut,
                            Model model) {
        return orderService.orderShow(orderId, employeeAut,model);
    }
    @PostMapping("/{id}/deleteGuest")
    public String deleteGuest(@PathVariable("id") Long orderId,@ModelAttribute("guestId")Long guestId){
        return orderService.deleteGuest(orderId,guestId);
    }
    @PostMapping("/{id}/deleteReserve")
    public String deleteReserve(@PathVariable("id") Long orderId){
        return orderService.reserveDelete(orderId);
    }
    @PostMapping("/{id}/addGuest")
    public String addGuest(@PathVariable("id") Long orderId) {
        return orderService.addGuest(orderId);
    }

    @PostMapping("/{id}/payment")
    public String payment(@PathVariable("id") Long orderId) {
        return orderService.payment(orderId);
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        return orderService.createOrder(employee);
    }
}
