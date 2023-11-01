package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final ReserveService reserveService;
    private final GuestService guestService;
    private final GameZoneService gameZoneService;
    private final EmployeeService employeeService;
    public OrderController(OrderService orderService, ReserveService reserveService, GuestService guestService, GameZoneService gameZoneService, EmployeeService employeeService) {
        this.orderService = orderService;
        this.reserveService = reserveService;
        this.guestService = guestService;
        this.gameZoneService = gameZoneService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}/reserve")
    public String showReserve(@PathVariable("id") Long orderId,
                              @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                              @RequestParam(value = "dayMonth", required = false) String dayMonth,
                              @AuthenticationPrincipal Employee employee,
                              Model model) {
        //создает лист игровых зон
        List<GameZone> gameZoneList = gameZoneService.getGameZoneList();
        //создает список дней для резервов
        List<String> dayOfReserve = reserveService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        if (dayMonth != null && gameZoneId != null) {
            Integer[][] freeTimesAndMaxHourReserve= new Integer[0][];
            try {
                Order order = orderService.getOrder(orderId);
                if(!employeeService.isAccessOrder(employee,order)){
                    return "noAccess";
                }
                GameZone gameZone = gameZoneService.getGameZone(gameZoneId);
                freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone,dayMonth);
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
    @PostMapping("/markForDeletion")
    public String markForDeletion(@RequestParam("orderId") Long orderId,
                                  @AuthenticationPrincipal Employee employee,
                                  RedirectAttributes redirectAttributes){
        Order order = null;
        try {
            order = orderService.getOrder(orderId);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        if(!employeeService.isAccessOrder(employee,order)){
            return "noAccess";
        }
        order.setTaggedDelete(true);
        orderService.save(order);
        return "redirect:/";
    }
    @PostMapping("/reserve/Add")
    public String addReserve(@RequestParam("orderId") Long orderId,
                             @ModelAttribute(value = "dayOfMount") String dayOfMount,
                             @ModelAttribute(value = "gameZoneId") Long gameZoneId,
                             @ModelAttribute(value = "freeTime") int freeTime,
                             @ModelAttribute(value = "maxHour") int maxHour,
                             @RequestParam(value = "hour", defaultValue = "0") int hour,
                             @AuthenticationPrincipal Employee employee,
                             RedirectAttributes redirectAttributes) {
        if (maxHour < hour || hour == 0) {
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        try {
            Order order = orderService.getOrder(orderId);
            if(!employeeService.isAccessOrder(employee,order)){
                return "noAccess";
            }
            reserveService.setReserve(order,dayOfMount,gameZoneId,freeTime,maxHour,hour);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + orderId;
    }
    @GetMapping("/{id}")
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employee,
                            @RequestParam(value = "errorGuestId",required = false) Long guestId,
                            @RequestParam(value = "errorGuestMessage",required = false) String message,
                            Model model) {
        Order order;
        List<Guest> guestList;
        List<GuestCart> guestCartList;
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        try {
            order = orderService.getOrder(orderId);
            guestList = guestService.getGuestListByOrder(order);
            guestCartList = guestService.getGuestCartListByGuest(guestList);
        } catch (NotFoundException e) {
            model.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        Optional<String> errorGuestMessage =Optional.ofNullable(message);
        Optional<Long> errorGuestId =Optional.ofNullable(guestId);
        model.addAttribute("errorGuestId",errorGuestId);
        model.addAttribute("errorGuestMessage",errorGuestMessage);
        model.addAttribute("user", optionalEmployee);
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal() / 10.0);
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        return "order/order";
    }
    @PostMapping("/deleteGuest")
    public String deleteGuest(@RequestParam("orderId") Long orderId,
                              @ModelAttribute("guestId")Long guestId,
                              @AuthenticationPrincipal Employee employee,
                              RedirectAttributes redirectAttributes){
        try{
            Order order = orderService.getOrder(orderId);
            if(!employeeService.isAccessOrder(employee,order)){
                return "noAccess";
            }
            orderService.checkPaymentOrder(order);
            Guest guest = guestService.getGuest(guestId,order);
            guestService.deleteGuest(guest);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        } catch (GuestsHaveGoodsException e) {
            redirectAttributes.addAttribute("errorGuestId",e.getGuestId());
            redirectAttributes.addAttribute("errorGuestMessage",e.getMessage());
            return "redirect:/order/"+orderId;
        }
        return "redirect:/order/"+orderId;
    }
    @PostMapping("/deleteReserve")
    public String deleteReserve(@RequestParam("orderId") Long orderId,
                                @AuthenticationPrincipal Employee employee,
                                RedirectAttributes redirectAttributes){
        try {
            Order order = orderService.getOrder(orderId);
            if(!employeeService.isAccessOrder(employee,order)){
                return "noAccess";
            }
            reserveService.deleteReserve(order);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + orderId;
    }
    @GetMapping("/notFound")
    public String notFound(@RequestParam("message")String message,Model model){
        model.addAttribute("message",message);
        return "order/notFound";
    }
    @PostMapping("/addGuest")
    public String addGuest(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes,@AuthenticationPrincipal Employee employee){
        try {
            Order order = orderService.getOrder(orderId);
            if(!employeeService.isAccessOrder(employee,order)){
                return "noAccess";
            }
            orderService.checkPaymentOrder(order);
            guestService.addGuest(order);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + orderId;
    }
    @PostMapping("/payment")
    public String payment(@RequestParam("orderId") Long orderId,RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrder(orderId);
            orderService.payment(order);
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message",e.getMessage());
            return "redirect:/order/notFound";
        }
        return "redirect:/order/" + orderId;
    }
    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        Order order = orderService.createOrder(employee);
        return "redirect:/order/" + order.getId();
    }
}
