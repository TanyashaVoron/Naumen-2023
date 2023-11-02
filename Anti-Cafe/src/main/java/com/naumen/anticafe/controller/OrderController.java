package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public OrderController(OrderService orderService,
                           ReserveService reserveService,
                           GuestService guestService,
                           GameZoneService gameZoneService,
                           EmployeeService employeeService) {
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
            try {
                //находит заказ и проверяет на доступ к изменению этим сотрудником
                Order order = orderService.getOrder(orderId);
                if (!employeeService.isAccessOrder(employee, order)) {
                    return "noAccess";
                }
                //передает игровую зону
                GameZone gameZone = gameZoneService.getGameZone(gameZoneId);
                //передает свободные часы для резерва и так же максимальное время для резерва
                Integer[][] freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone, dayMonth);
                model.addAttribute("gameZoneId", gameZoneId);
                model.addAttribute("dayMonth", dayMonth);
                model.addAttribute("freeTimes", freeTimesAndMaxHourReserve);
            } catch (NotFoundException e) {
                model.addAttribute("message", e.getMessage());
                return "redirect:/order/notFound";
            }
        }
        model.addAttribute("user", Optional.ofNullable(employee));
        model.addAttribute("orderId", orderId);
        model.addAttribute("gameZones", gameZoneList);
        model.addAttribute("dayOfReserve", dayOfReserve);
        return "order/reserve";
    }

    @PostMapping("/markForDeletion")
    public String markForDeletion(@RequestParam("orderId") Long orderId,
                                  @AuthenticationPrincipal Employee employee,
                                  RedirectAttributes redirectAttributes) {
        try {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            if (!employeeService.isAccessOrder(employee, order)) {
                return "noAccess";
            }
            //маркирует заказ на удаление
            order.setTaggedDelete(true);
            orderService.save(order);
            return "redirect:/";
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
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
        //проверяет на корректность переданных данных
        if (maxHour < hour || hour == 0) {
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        try {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            if (!employeeService.isAccessOrder(employee, order)) {
                return "noAccess";
            }
            reserveService.setReserve(order, dayOfMount, gameZoneId, freeTime, maxHour, hour);
            return "redirect:/order/" + orderId;
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @GetMapping("/{id}")
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employee,
                            @RequestParam(value = "errorGuestId", required = false) Long guestId,
                            @RequestParam(value = "errorGuestMessage", required = false) String message,
                            Model model) {
        try {
            Order order = orderService.getOrder(orderId);
            List<Guest> guestList = guestService.getGuestListByOrder(order);
            List<GuestCart> guestCartList = guestService.getGuestCartListByGuest(guestList);
            Optional<String> errorGuestMessage = Optional.ofNullable(message);
            Optional<Long> errorGuestId = Optional.ofNullable(guestId);
            model.addAttribute("errorGuestId", errorGuestId);
            model.addAttribute("errorGuestMessage", errorGuestMessage);
            model.addAttribute("user", Optional.ofNullable(employee));
            model.addAttribute("order", order);
            model.addAttribute("total", order.getTotal() / 10.0);
            model.addAttribute("guests", guestList);
            model.addAttribute("products", guestCartList);
            return "order/order";
        } catch (NotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/deleteGuest")
    public String deleteGuest(@RequestParam("orderId") Long orderId,
                              @ModelAttribute("guestId") Long guestId,
                              @AuthenticationPrincipal Employee employee,
                              RedirectAttributes redirectAttributes) {
        try {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            if (!employeeService.isAccessOrder(employee, order)) {
                return "noAccess";
            }
            //проверяет оплату
            orderService.checkPaymentOrder(order);
            //удаляет гостя без товаров
            Guest guest = guestService.getGuest(guestId, order);
            guestService.deleteGuest(guest);
            return "redirect:/order/" + orderId;
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        } catch (GuestsHaveGoodsException e) {
            redirectAttributes.addAttribute("errorGuestId", e.getGuestId());
            redirectAttributes.addAttribute("errorGuestMessage", e.getMessage());
            return "redirect:/order/" + orderId;
        }
    }

    @PostMapping("/deleteReserve")
    public String deleteReserve(@RequestParam("orderId") Long orderId,
                                @AuthenticationPrincipal Employee employee,
                                RedirectAttributes redirectAttributes) {
        try {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            if (!employeeService.isAccessOrder(employee, order)) {
                return "noAccess";
            }
            reserveService.deleteReserve(order);
            return "redirect:/order/" + orderId;
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @GetMapping("/notFound")
    public String notFound(@RequestParam("message") String message, Model model) {
        model.addAttribute("message", message);
        return "order/notFound";
    }

    @PostMapping("/addGuest")
    public String addGuest(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Employee employee) {
        try {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            if (!employeeService.isAccessOrder(employee, order)) {
                return "noAccess";
            }
            orderService.checkPaymentOrder(order);
            guestService.addGuest(order);
            return "redirect:/order/" + orderId;
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/payment")
    public String payment(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrder(orderId);
            orderService.payment(order);
            return "redirect:/order/" + orderId;
        } catch (NotFoundException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/order/notFound";
        }
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        Order order = orderService.createOrder(employee);
        return "redirect:/order/" + order.getId();
    }
}
