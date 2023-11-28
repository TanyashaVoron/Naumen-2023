package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.send.order.GameZoneReserveDTO;
import com.naumen.anticafe.DTO.send.order.GuestCartOrderDTO;
import com.naumen.anticafe.DTO.send.order.GuestOrderDTO;
import com.naumen.anticafe.DTO.receive.order.*;
import com.naumen.anticafe.DTO.send.order.ShowReserveSendDTO;
import com.naumen.anticafe.DTO.send.order.ShowSendDTO;
import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.NoAccessToOperation;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.AccessService;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.guest.GuestService;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import com.naumen.anticafe.service.order.OrderService;
import com.naumen.anticafe.service.order.PaymentOrderService;
import com.naumen.anticafe.service.order.reserve.ReserveService;
import com.naumen.anticafe.serviceImpl.order.PaymentOrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final EmployeeService employeeService;
    private final GuestCartService guestCartService;
    private final PaymentOrderService paymentOrderService;

    @Autowired
    public OrderController(OrderService orderService,
                           ReserveService reserveService,
                           GuestService guestService,
                           GameZoneService gameZoneService, AccessService accessService, EmployeeService employeeService, GuestCartService guestCartService, PaymentOrderServiceImpl paymentOrderService) {
        this.orderService = orderService;
        this.reserveService = reserveService;
        this.guestService = guestService;
        this.gameZoneService = gameZoneService;
        this.accessService = accessService;
        this.employeeService = employeeService;
        this.guestCartService = guestCartService;
        this.paymentOrderService = paymentOrderService;
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal(expression = "username") String employeeUsername,
                            @ModelAttribute ShowDTO dto,
                            Model model) throws NotFoundException {
        Order order = orderService.getOrder(orderId);
        Employee employee = employeeService.searchEmployee(employeeUsername);
        boolean access = accessService.isAccessOrder(employee, order);
        Optional<String> gameZoneName;
        String reserveDate;
        String reserveTime;
        String endReserve;
        long gameZoneId;
        List<GuestOrderDTO> guestsList = new ArrayList<>();
        for (Guest g : order.getGuests()) {
            GuestOrderDTO guestOrderDTO = new GuestOrderDTO(
                    g.getCompositeId().getGuestId(),
                    g.getName());
            guestsList.add(guestOrderDTO);
        }
        List<GuestCartOrderDTO> guestCartList = new ArrayList<>();
        for (GuestCart gc : guestCartService.getGuestCartListByOrder(order)) {
            GuestCartOrderDTO guestCartOrderDTO = new GuestCartOrderDTO(
                    gc.getId(),
                    gc.getProduct().getName(),
                    gc.getQuantity(),
                    gc.getGuest().getName()
            );
            guestCartList.add(guestCartOrderDTO);
        }
        if (order.getGameZone() == null) {
            gameZoneName = Optional.empty();
            reserveDate = null;
            reserveTime = null;
            endReserve = null;
            gameZoneId = -1;
        } else {
            gameZoneName = Optional.of(order.getGameZone().getName());
            reserveDate = order.getReserveDate().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"));
            reserveTime = order.getReserveTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            endReserve = order.getEndReserve().format(DateTimeFormatter.ofPattern("HH:mm"));
            gameZoneId = order.getGameZone().getId();
        }
        ShowSendDTO sendDTO = new ShowSendDTO(
                guestsList,
                guestCartList,
                Optional.ofNullable(dto.guestIdError()),
                Optional.ofNullable(dto.guestMessageError()),
                employee.getName(),
                order.getManager().getName(),
                gameZoneName,
                reserveDate,
                reserveTime,
                endReserve,
                order.getTaggedDelete(),
                order.getPayment(),
                order.getTotal(),
                orderId,
                gameZoneId,
                access
        );
        model.addAttribute("sendDTO", sendDTO);
        return "order/order";
    }

    @GetMapping("/{id}/reserve")
    @Transactional(readOnly = true)
    public String showReserve(@PathVariable("id") Long orderId,
                              @ModelAttribute ShowReserveDTO dto,
                              @AuthenticationPrincipal(expression = "username") String employeeUsername,
                              Model model) throws NotFoundException, NoAccessToOperation {
        Employee employee = employeeService.searchEmployee(employeeUsername);
        Order order = orderService.getOrder(orderId);
        if (!accessService.isAccessOrder(employee, order))
            throw new NoAccessToOperation(
                    "У вас нет доступа. Обратитесь к владельцу, Администратору или главному менеджеру",
                    order.getManager().getName(),
                    employee.getName()
            );
        //создает лист игровых зон
        List<GameZoneReserveDTO> gameZoneList = new ArrayList<>();
        for (GameZone gz : gameZoneService.getGameZoneList())
            gameZoneList.add(new GameZoneReserveDTO(gz.getId(), gz.getName()));
        //создает список дней для резервов
        List<String> dayOfReserve = reserveService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        int[][] freeTimesAndMaxHourReserve = null;
        if (dto.dayMonth() != null && dto.gameZoneId() != null) {
            //передает игровую зону
            GameZone gameZone = gameZoneService.getGameZone(dto.gameZoneId());
            //передает свободные часы для резерва и так же максимальное время для резерва
            freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone, dto.dayMonth());
        }
        ShowReserveSendDTO sendDTO = new ShowReserveSendDTO(
                employee.getName(),
                orderId,
                gameZoneList,
                dayOfReserve,
                dto.gameZoneId(),
                dto.dayMonth(),
                Optional.ofNullable(freeTimesAndMaxHourReserve),
                Optional.ofNullable(dto.hourError()),
                dto.hourMessageError()
        );
        model.addAttribute("sendDTO", sendDTO);
        return "order/reserve";
    }

    @PostMapping("/markForDeletion")
    @Transactional
    public String markForDeletion(@ModelAttribute MarkForDeletionDTO dto) throws NotFoundException {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(dto.orderId());
        //маркирует заказ на удаление
        order.setTaggedDelete(true);
        order.setTimerTaggedDelete(LocalDate.now());
        orderService.save(order);
        return "redirect:/";
    }

    @PostMapping("/reserve/Add")
    @Transactional
    public String addReserve(@RequestParam("orderId") Long orderId,
                             @Valid @ModelAttribute AddReserveDTO dto,
                             BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return "redirect:/order/" +
                    orderId +
                    "/reserve?dayMonth=" + dto.dayOfMount() +
                    "&gameZoneId=" + dto.gameZoneId() +
                    "&hourError=" + dto.freeTime() +
                    "&hourMessageError=" + UriComponentsBuilder.fromUriString(
                    bindingResult.getFieldErrors().get(0).getDefaultMessage()).build().encode();
        }
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        reserveService.setReserve(order, dto.dayOfMount(), dto.gameZoneId(), dto.freeTime(), dto.maxHour(), dto.hour());
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }


    @PostMapping("/deleteGuest")
    @Transactional
    public String deleteGuest(@ModelAttribute DeleteGuestDTO dto,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(dto.orderId());
        //проверяет оплату
        paymentOrderService.checkPaymentOrder(order);
        //удаляет гостя без товаров
        Guest guest = guestService.getGuest(dto.guestId(), order);
        long IdGuestCountNotNull = guestService.deleteGuest(guest);
        if (IdGuestCountNotNull != 0) {
            redirectAttributes.addAttribute("guestIdError", IdGuestCountNotNull);
            redirectAttributes.addAttribute("guestMessageError", "У гостя есть товары");
            return "redirect:/order/" + dto.orderId();
        }
        return "redirect:/order/" + dto.orderId();
    }

    @PostMapping("/deleteReserve")
    @Transactional
    public String deleteReserve(@ModelAttribute DeleteReserveDTO dto) throws NotFoundException {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(dto.orderId());
        reserveService.deleteReserve(order);
        orderService.save(order);
        return "redirect:/order/" + dto.orderId();
    }

    @PostMapping("/addGuest")
    @Transactional
    public String addGuest(@ModelAttribute AddGuestDTO dto) throws NotFoundException {
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(dto.orderId());
        paymentOrderService.checkPaymentOrder(order);
        guestService.addGuest(order);
        return "redirect:/order/" + dto.orderId();
    }

    @PostMapping("/payment")
    @Transactional
    public String payment(@ModelAttribute PaymentDTO dto) throws NotFoundException {
        Order order = orderService.getOrder(dto.orderId());
        paymentOrderService.payment(order);
        orderService.save(order);
        return "redirect:/order/" + dto.orderId();
    }

    @PostMapping("/create")
    @Transactional
    public String createOrder(@AuthenticationPrincipal(expression = "username") String employeeUsername) throws NotFoundException {
        Order order = orderService.createOrder(employeeService.searchEmployee(employeeUsername));
        orderService.save(order);
        return "redirect:/order/" + order.getId();
    }
}
