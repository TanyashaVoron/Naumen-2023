package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.object.GameZoneReserveDTO;
import com.naumen.anticafe.DTO.object.GuestCartOrderDTO;
import com.naumen.anticafe.DTO.object.GuestOrderDTO;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriBuilder;
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
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal(expression = "username") String employeeUsername,
                            @ModelAttribute ShowDTO DTO,
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
            gameZoneName = Optional.ofNullable(null);
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
                Optional.ofNullable(DTO.getGuestIdError()),
                Optional.ofNullable(DTO.getGuestMessageError()),
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
    public String showReserve(@PathVariable("id") Long orderId,
                              @ModelAttribute ShowReserveDTO DTO,
                              @AuthenticationPrincipal(expression = "username") String employeeUsername,
                              Model model) throws NotFoundException, NoAccessToOperation {
        Employee employee = employeeService.searchEmployee(employeeUsername);
        Order order = orderService.getOrder(orderId);
        if(!accessService.isAccessOrder(employee,order))
            throw new NoAccessToOperation(
                    "У вас нет доступа. Обратитесь к владельцу, Администратору или главному менеджеру",
                    order.getManager().getName(),
                    employee.getName()
            );
        //создает лист игровых зон
        List<GameZoneReserveDTO> gameZoneList = new ArrayList<>();
        for(GameZone gz : gameZoneService.getGameZoneList())
            gameZoneList.add(new GameZoneReserveDTO(gz.getId(),gz.getName()));
        //создает список дней для резервов
        List<String> dayOfReserve = reserveService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        ShowReserveSendDTO sendDTO = new ShowReserveSendDTO(
                employee.getName(),
                orderId,
                gameZoneList,
                dayOfReserve,
                Optional.ofNullable(null),
                Optional.ofNullable(DTO.getHourError()),
                DTO.getHourMessageError()
        );
        if (DTO.getDayMonth()!=null && DTO.getGameZoneId()!= null) {
            //передает игровую зону
            GameZone gameZone = gameZoneService.getGameZone(DTO.getGameZoneId());
            //передает свободные часы для резерва и так же максимальное время для резерва
            int[][] freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone, DTO.getDayMonth());
            sendDTO.setGameZoneId(DTO.getGameZoneId());
            sendDTO.setDayMonth(DTO.getDayMonth());
            sendDTO.setFreeTimes(Optional.of(freeTimesAndMaxHourReserve));
        }
        model.addAttribute("sendDTO",sendDTO);
        return "order/reserve";
    }

    @PostMapping("/markForDeletion")
    public String markForDeletion(@ModelAttribute MarkForDeletionDTO DTO) throws NotFoundException{
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(DTO.getOrderId());
        //маркирует заказ на удаление
        order.setTaggedDelete(true);
        order.setTimerTaggedDelete(LocalDate.now());
        orderService.save(order);
        return "redirect:/";
    }

    @PostMapping("/reserve/Add")
    public String addReserve(@RequestParam("orderId") Long orderId,
                             @Valid @ModelAttribute AddReserveDTO DTO,
                             BindingResult bindingResult) throws NotFoundException{
        if(bindingResult.hasErrors()){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("redirect:/order/")
                    .append(orderId)
                    .append("/reserve?dayMonth=").append(DTO.getDayOfMount())
                    .append("&gameZoneId=").append(DTO.getGameZoneId())
                    .append("&hourError=").append(DTO.getFreeTime())
                    .append("&hourMessageError=").append(UriComponentsBuilder.fromUriString(
                            bindingResult.getFieldErrors().get(0).getDefaultMessage()).build().encode());
            return  stringBuilder.toString();
        }
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(orderId);
        reserveService.setReserve(order, DTO.getDayOfMount(), DTO.getGameZoneId(), DTO.getFreeTime(), DTO.getMaxHour(), DTO.getHour());
        orderService.save(order);
        return "redirect:/order/" + orderId;
    }


    @PostMapping("/deleteGuest")
    public String deleteGuest(@ModelAttribute DeleteGuestDTO DTO,
                              RedirectAttributes redirectAttributes) throws NotFoundException{
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(DTO.getOrderId());
        //проверяет оплату
        paymentOrderService.checkPaymentOrder(order);
        //удаляет гостя без товаров
        Guest guest = guestService.getGuest(DTO.getGuestId(), order);
        long IdGuestCountNotNull = guestService.deleteGuest(guest);
        if (IdGuestCountNotNull != 0) {
            redirectAttributes.addAttribute("errorGuestId", IdGuestCountNotNull);
            redirectAttributes.addAttribute("errorGuestMessage", "У гостя есть товары");
            return "redirect:/order/" + DTO.getOrderId();
        }
        return "redirect:/order/" + DTO.getOrderId();
    }

    @PostMapping("/deleteReserve")
    public String deleteReserve(@ModelAttribute DeleteReserveDTO DTO) throws NotFoundException{
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(DTO.getOrderId());
        reserveService.deleteReserve(order);
        orderService.save(order);
        return "redirect:/order/" + DTO.getOrderId();
    }

    @PostMapping("/addGuest")
    public String addGuest(@ModelAttribute AddGuestDTO DTO) throws NotFoundException{
        //находит заказ и проверяет на доступ к изменению этим сотрудником
        Order order = orderService.getOrder(DTO.getOrderId());
        paymentOrderService.checkPaymentOrder(order);
        guestService.addGuest(order);
        return "redirect:/order/" + DTO.getOrderId();
    }

    @PostMapping("/payment")
    public String payment(@ModelAttribute PaymentDTO DTO) throws NotFoundException {
        Order order = orderService.getOrder(DTO.getOrderId());
        paymentOrderService.payment(order);
        orderService.save(order);
        return "redirect:/order/" + DTO.getOrderId();
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal(expression = "username") String employeeUsername) throws NotFoundException {
        Order order = orderService.createOrder(employeeService.searchEmployee(employeeUsername));
        orderService.save(order);
        return "redirect:/order/" + order.getId();
    }
}
