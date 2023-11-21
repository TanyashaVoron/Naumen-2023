package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;

import com.naumen.anticafe.error.NoAccessToOperation;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.AccessService;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.guest.GuestService;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import com.naumen.anticafe.service.order.OrderService;
import com.naumen.anticafe.service.order.reserve.ReserveService;
import com.naumen.anticafe.serviceImpl.order.PaymentOrderServiceImpl;
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
    private final AccessService accessService;
    private final GuestCartService guestCartService;
    private final PaymentOrderServiceImpl paymentOrderService;

    @Autowired
    public OrderController(OrderService orderService,
                           ReserveService reserveService,
                           GuestService guestService,
                           GameZoneService gameZoneService, AccessService accessService, GuestCartService guestCartService, PaymentOrderServiceImpl paymentOrderService) {
        this.orderService = orderService;
        this.reserveService = reserveService;
        this.guestService = guestService;
        this.gameZoneService = gameZoneService;
        this.accessService = accessService;
        this.guestCartService = guestCartService;
        this.paymentOrderService = paymentOrderService;
    }

    @GetMapping("/{id}/reserve")
    public String showReserve(@PathVariable("id") Long orderId,
                              @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                              @RequestParam(value = "dayMonth", required = false) String dayMonth,
                              @AuthenticationPrincipal Employee employee,
                              Model model) throws NotFoundException, NoAccessToOperation {
        //создает лист игровых зон
        List<GameZone> gameZoneList = gameZoneService.getGameZoneList();
        //создает список дней для резервов
        List<String> dayOfReserve = reserveService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        if (dayMonth != null && gameZoneId != null) {
            //находит заказ и проверяет на доступ к изменению этим сотрудником
            Order order = orderService.getOrder(orderId);
            accessService.isAccessOrder(employee, order);
            //передает игровую зону
            GameZone gameZone = gameZoneService.getGameZone(gameZoneId);
            //передает свободные часы для резерва и так же максимальное время для резерва
            Integer[][] freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone, dayMonth);
            model.addAttribute("gameZoneId", gameZoneId);
            model.addAttribute("dayMonth", dayMonth);
            model.addAttribute("freeTimes", freeTimesAndMaxHourReserve);
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
                                  RedirectAttributes redirectAttributes) throws NotFoundException, NoAccessToOperation {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        accessService.isAccessOrder(employee, order);
        //маркирует заказ на удаление
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
                             RedirectAttributes redirectAttributes) throws NotFoundException, NoAccessToOperation {
        //проверяет на корректность переданных данных
        if (maxHour < hour || hour == 0) {
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        accessService.isAccessOrder(employee, order);
        reserveService.setReserve(order, dayOfMount, gameZoneId, freeTime, maxHour, hour);
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/{id}")
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employee,
                            @RequestParam(value = "errorGuestId", required = false) Long guestId,
                            @RequestParam(value = "errorGuestMessage", required = false) String message,
                            Model model) throws NotFoundException {
        Order order = orderService.getOrder(orderId);
        List<Guest> guestList = order.getGuests();
        Guest guest = guestList.get(0);
        guestService.deleteGuestCascade(guest);
        List<GuestCart> guestCartList = guestCartService.getGuestCartListByOrder(order);
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
    }

    @PostMapping("/deleteGuest")
    public String deleteGuest(@RequestParam("orderId") Long orderId,
                              @ModelAttribute("guestId") Long guestId,
                              @AuthenticationPrincipal Employee employee,
                              RedirectAttributes redirectAttributes) throws NotFoundException, NoAccessToOperation {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        accessService.isAccessOrder(employee, order);
        //проверяет оплату
        paymentOrderService.checkPaymentOrder(order);
        //удаляет гостя без товаров
        Guest guest = guestService.getGuest(guestId, order);
        long IdGuestCountNotNull = guestService.deleteGuest(guest);
        if (IdGuestCountNotNull != 0) {
            redirectAttributes.addAttribute("errorGuestId", IdGuestCountNotNull );
            redirectAttributes.addAttribute("errorGuestMessage", "У гостя есть товары");
            return "redirect:/order/" + orderId;
        }
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/deleteReserve")
    public String deleteReserve(@RequestParam("orderId") Long orderId,
                                @AuthenticationPrincipal Employee employee,
                                RedirectAttributes redirectAttributes) throws NotFoundException, NoAccessToOperation {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        accessService.isAccessOrder(employee, order);
        reserveService.deleteReserve(order);
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }
    @PostMapping("/addGuest")
    public String addGuest(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Employee employee) throws NotFoundException, NoAccessToOperation {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        accessService.isAccessOrder(employee, order);
        paymentOrderService.checkPaymentOrder(order);
        guestService.addGuest(order);
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/payment")
    public String payment(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes) throws NotFoundException {
        Order order = orderService.getOrder(orderId);
        paymentOrderService.payment(order);
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        Order order = orderService.createOrder(employee);
        return "redirect:/order/" + order.getId();
    }
}
