package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.order.*;
import com.naumen.anticafe.DTO.send.order.*;
import com.naumen.anticafe.converter.ConverterOrder;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.GuestId;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.exception.NoAccessToOperation;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.exception.ReserveException;
import com.naumen.anticafe.properties.ReserveServiceProperties;
import com.naumen.anticafe.service.AccessService;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.guest.GuestService;
import com.naumen.anticafe.service.guestCart.GuestCartService;
import com.naumen.anticafe.service.order.MarkDeletionOrderService;
import com.naumen.anticafe.service.order.OrderService;
import com.naumen.anticafe.service.order.PaymentOrderService;
import com.naumen.anticafe.service.order.reserve.ReserveService;
import com.naumen.anticafe.serviceImpl.order.PaymentOrderServiceImpl;
import jakarta.validation.Valid;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final ReserveService reserveService;
    private final GuestService guestService;
    private final GameZoneService gameZoneService;
    private final AccessService accessService;
    private final GuestCartService guestCartService;
    private final PaymentOrderService paymentOrderService;
    private final ReserveServiceProperties reserveServiceProperties;
    private final MarkDeletionOrderService markForDeletion;

    @Autowired
    public OrderController(OrderService orderService,
                           ReserveService reserveService,
                           GuestService guestService,
                           GameZoneService gameZoneService, AccessService accessService, GuestCartService guestCartService, PaymentOrderServiceImpl paymentOrderService, ReserveServiceProperties reserveServiceProperties, MarkDeletionOrderService markForDeletion) {
        this.orderService = orderService;
        this.reserveService = reserveService;
        this.guestService = guestService;
        this.gameZoneService = gameZoneService;
        this.accessService = accessService;
        this.guestCartService = guestCartService;
        this.paymentOrderService = paymentOrderService;
        this.reserveServiceProperties = reserveServiceProperties;
        this.markForDeletion = markForDeletion;
    }

    /**
     * отображение заказа
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String showOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal Employee employee,
                            @ModelAttribute ShowDTO dto,
                            Model model) throws NotFoundException {
        Order order = orderService.getOrder(orderId);
        //проверяет доступ к заказу
        boolean access = accessService.isAccessOrder(employee, order);
        //если доступ есть провверяет оплату
        access = access ? !order.getPayment() : access;
        List<GuestOrderDTO> guestsList = ConverterOrder.convertToListGuestOrderDTO(order.getGuests());
        List<GuestCartOrderDTO> guestCartList = ConverterOrder
                .convertToListGuestCartOrderDTO(guestCartService.getGuestCartListByOrder(order));
        ShowSendDTO sendDTO = ConverterOrder
                .convertToShowSendDTO(
                        order,
                        guestsList,
                        guestCartList,
                        dto,
                        employee,
                        access
                );
        model.addAttribute("sendDTO", sendDTO);
        return "order/order";
    }

    /**
     * отображение страницы для резервирования
     */
    @GetMapping("/{id}/reserve")
    @Transactional(readOnly = true)
    public String showReserve(@PathVariable("id") Long orderId,
                              @ModelAttribute ShowReserveDTO dto,
                              @AuthenticationPrincipal Employee employee,
                              Model model) throws NotFoundException, NoAccessToOperation {
        Order order = orderService.getOrder(orderId);
        //проверяет доступ к заказу
        if (!accessService.isAccessOrder(employee, order))
            throw new NoAccessToOperation(
                    "У вас нет доступа. Обратитесь к владельцу, Администратору или главному менеджеру",
                    order.getManager().getName(),
                    employee.getName()
            );
        //создает лист игровых зон
        List<GameZoneReserveDTO> gameZoneList = ConverterOrder
                .convertToListGameZoneReserveDTO(gameZoneService.getGameZoneList());
        //создает список дней для резервов
        List<String> dayOfReserve = reserveService.getAllDayOfReserve();
        //проверяем передачу дня и игровой зоны
        int[][] freeTimesAndMaxHourReserve = null;
        if (dto.day() != null && dto.gameZoneId() != null) {
            //передает игровую зону
            GameZone gameZone = gameZoneService.getGameZone(dto.gameZoneId());
            LocalDate dayReserve = ConverterOrder.converterToLocalDate(dto.day());
            //передает свободные часы для резерва и так же максимальное время для резерва
            freeTimesAndMaxHourReserve = reserveService.getFreeTimesAndMaxHourReserve(gameZone, dayReserve);
        }
        ShowReserveSendDTO sendDTO = ConverterOrder.convertToShowReserveSendDTO(
                employee,
                orderId,
                gameZoneList,
                dayOfReserve,
                freeTimesAndMaxHourReserve,
                dto
        );
        model.addAttribute("sendDTO", sendDTO);
        return "order/reserve";
    }

    /**
     * Маркирует на удаление
     */
    @PostMapping("/markForDeletion")
    @Transactional
    public String markForDeletion(@ModelAttribute MarkForDeletionDTO dto) {
        markForDeletion.markForDeletion(dto.orderId(), true, LocalDate.now());
        return "redirect:/";
    }

    /**
     * Добавляет в заказ резерв
     */
    @PostMapping("/reserve/Add")
    @Retryable(retryFor = PSQLException.class, maxAttempts = 100, backoff = @Backoff(delay = 100))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String addReserve(@RequestParam("orderId") Long orderId,
                             @Valid @ModelAttribute AddReserveDTO dto,
                             BindingResult bindingResult) throws NotFoundException, ReserveException {
        //если есть ошибки переадресует с указанием ошибок в get
        if (bindingResult.hasErrors()) {
            return "redirect:/order/" +
                    orderId +
                    "/reserve?dayMonth=" + dto.day() +
                    "&gameZoneId=" + dto.gameZoneId() +
                    "&hourError=" + dto.freeTime() +
                    "&hourMessageError=" + UriComponentsBuilder.fromUriString(
                    bindingResult.getFieldErrors().get(0).getDefaultMessage()).build().encode();
        }
        LocalDate date = ConverterOrder.converterToLocalDate(dto.day());
        //определяет корректный ли день для резерва
        if (date.isAfter(LocalDate.now().plusDays(reserveServiceProperties.getDaysToReserve())))
            throw new ReserveException("Не корректный день");
        GameZone gameZone = gameZoneService.getGameZone(dto.gameZoneId());
        //проверяет свободен ли резервируемый фрагмент
        if (!reserveService.checkReserve(date, dto.freeTime(), dto.hour(), gameZone))
            throw new ReserveException("Резервируемые данные устарели");
        Order order = orderService.getOrder(orderId);
        reserveService.setReserve(order, date, gameZone, dto.freeTime(), dto.hour());
        return "redirect:/order/" + orderId;
    }

    /**
     * удаляет гостя
     */
    @PostMapping("/deleteGuest")
    @Transactional
    public String deleteGuest(@ModelAttribute DeleteGuestDTO dto,
                              RedirectAttributes redirectAttributes) throws NotFoundException {
        Order order = orderService.getOrder(dto.orderId());
        //удаляет гостя по композитному ключу, если гость не был удален вернет 0
        if (guestService.deleteGuestByNotExistCart(new GuestId(dto.guestId(), order)) == 0) {
            redirectAttributes.addAttribute("guestIdError", dto.guestId());
            redirectAttributes.addAttribute("guestMessageError", "У гостя есть товары");
            return "redirect:/order/" + dto.orderId();
        }
        return "redirect:/order/" + dto.orderId();
    }

    /**
     * удаляет резерв
     */
    @PostMapping("/deleteReserve")
    @Transactional
    public String deleteReserve(@ModelAttribute DeleteReserveDTO dto) {
        reserveService.deleteReserve(dto.orderId());
        return "redirect:/order/" + dto.orderId();
    }

    /**
     * добавляет гостя
     */
    @PostMapping("/addGuest")
    @Transactional
    public String addGuest(@ModelAttribute AddGuestDTO dto) throws NotFoundException {
        Order order = orderService.getOrder(dto.orderId());
        guestService.addGuest(order);
        return "redirect:/order/" + dto.orderId();
    }

    /**
     * оплачивает заказ
     */
    @PostMapping("/payment")
    @Transactional
    public String payment(@ModelAttribute PaymentDTO dto) throws NotFoundException {
        Order order = orderService.getOrder(dto.orderId());
        paymentOrderService.payment(order);
        return "redirect:/order/" + dto.orderId();
    }

    /**
     * создает заказ
     */
    @PostMapping("/create")
    @Transactional
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        Order order = orderService.createOrder(employee);
        return "redirect:/order/" + order.getId();
    }
}
