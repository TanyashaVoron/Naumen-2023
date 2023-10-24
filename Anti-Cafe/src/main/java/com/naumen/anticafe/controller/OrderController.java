package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {

        this.orderService = orderService;
    }
    @GetMapping("/{id}/reserve")
    public String showReserve(@PathVariable("id") Long orderId,
                              @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                              @RequestParam(value = "dayMonth", required = false) String dayMonth,
                              @AuthenticationPrincipal Employee employee,
                              Model model) {
        //создает лист игровых зон
        List<GameZone> gameZoneList = orderService.getGameZoneList();
        //создает список дней для резервов
        List<String> dayOfReserve = orderService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        if (dayMonth != null && gameZoneId != null) {
            Integer[][] freeTimesAndMaxHourReserve= new Integer[0][];
            try {
                freeTimesAndMaxHourReserve = orderService.getFreeTimesAndMaxHourReserve(gameZoneId,dayMonth);
            } catch (NotFoundException e) {
                model.addAttribute("message",e.getMessage());
                return "redirect:/order/notFound";
            }
            model.addAttribute("gameZoneId", gameZoneId);
            model.addAttribute("dayMonth", dayMonth);
            model.addAttribute("freeTimes", freeTimesAndMaxHourReserve);
        }
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user", optionalEmployee);
        model.addAttribute("orderId", orderId);
        model.addAttribute("gameZones", gameZoneList);
        model.addAttribute("dayOfReserve", dayOfReserve);
        return "order/reserve";
    }
    @PostMapping("/{id}/reserve/Add")
    public String addReserve(@PathVariable("id") Long orderId,
                             @ModelAttribute(value = "dayOfMount") String dayOfMount,
                             @ModelAttribute(value = "gameZoneId") Long gameZoneId,
                             @ModelAttribute(value = "freeTime") int freeTime,
                             @ModelAttribute(value = "maxHour") int maxHour,
                             @ModelAttribute(value = "hour") String hours,
                             RedirectAttributes redirectAttributes) {
        if(hours.equals("")){
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        int hour = Integer.parseInt(hours);
        if (maxHour < hour || hour <= 0) {
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        Order order;
        try {
            order = orderService.setReserve(orderId,dayOfMount,gameZoneId,freeTime,maxHour,hour);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + order.getId();
    }
    @GetMapping("/{id}")
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employeeAut,
                            @RequestParam(value = "errorGuestId",required = false) Long guestId,
                            @RequestParam(value = "errorGuestMessage",required = false) String message,
                            Model model) {
        Order order;
        List<Guest> guestList;
        List<GuestCart> guestCartList;
        Optional<Employee> optionalEmployeeAut = Optional.ofNullable(employeeAut);
        try {
            order = orderService.getOrder(orderId);
            guestList = orderService.getGuestListByOrder(order);
            guestCartList = orderService.getGuestCartListByGuest(guestList);
        } catch (NotFoundException e) {
            model.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        Optional<String> eGuestMessage =Optional.ofNullable(message);
        Optional<Long> eGuestId =Optional.ofNullable(guestId);
        model.addAttribute("eGuestId",eGuestId);
        model.addAttribute("eGuestMessage",eGuestMessage);
        model.addAttribute("user", optionalEmployeeAut);
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal() / 10.0);
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        return "order/order";
    }
    @PostMapping("/{id}/deleteGuest")
    public String deleteGuest(@PathVariable("id") Long orderId,
                              @ModelAttribute("guestId")Long guestId,
                              RedirectAttributes redirectAttributes){
        Order order;
        try{
            order = orderService.deleteGuest(orderId,guestId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        } catch (GuestsHaveGoodsException e) {
            redirectAttributes.addAttribute("errorGuestId",e.getGuestId());
            redirectAttributes.addAttribute("errorGuestMessage",e.getMessage());
            return "redirect:/order/"+orderId;
        }
        return "redirect:/order/"+order.getId();
    }
    @PostMapping("/{id}/deleteReserve")
    public String deleteReserve(@PathVariable("id") Long orderId,RedirectAttributes redirectAttributes){
        Order order;
        try {
            order = orderService.deleteReserve(orderId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + order.getId();
    }
    @GetMapping("/notFound")
    public String notFound(@RequestParam("message")String message,Model model){
        model.addAttribute("message",message);
        return "order/notFound";
    }
    @PostMapping("/{id}/addGuest")
    public String addGuest(@PathVariable("id") Long orderId, RedirectAttributes redirectAttributes) {
        Order order;
        try {
            order = orderService.addGuest(orderId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + order.getId();
    }
    @PostMapping("/{id}/payment")
    public String payment(@PathVariable("id") Long orderId,RedirectAttributes redirectAttributes) {
        Order order;
        try {
            order = orderService.payment(orderId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + order.getId();
    }
    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        Order order = orderService.createOrder(employee);
        return "redirect:/order/" + order.getId();
    }
}
