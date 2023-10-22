package com.naumen.anticafe.controller;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.GuestCartRepository;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.repository.OrderRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    private final GuestRepository guestRepository;
    private final GuestCartRepository guestCartRepository;
    private final GameZoneRepository gameZoneRepository;

    public OrderController(OrderRepository orderRepository,
                           GuestRepository guestRepository,
                           GuestCartRepository guestCartRepository,
                           GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.guestRepository = guestRepository;
        this.guestCartRepository = guestCartRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    @GetMapping("/{id}/reserve")
    public String reserveShow(@PathVariable("id") Long orderId,
                              @RequestParam(value = "gameZoneId", required = false) Long gameZoneId,
                              @RequestParam(value = "dayMonth", required = false) String dayMonth,
                              @AuthenticationPrincipal Employee employee,
                              Model model) {
        //создает лист игровых зон
        List<GameZone> gameZoneList = new ArrayList<>(gameZoneRepository.findAll());
        //создает список дней для резервов
        List<String> dayOfReserve = new ArrayList<>();
        //создаем переменную текущего дня для заполнения
        LocalDate date = LocalDate.now();
        //вносим в лист все последующее 7 дней
        for (int i = 0; i < 7; i++) {
            LocalDate dateNow = date.plusDays(i);
            dayOfReserve.add(dateNow.getDayOfMonth() + "." + dateNow.getMonthValue());
        }
        //проверяем передачу дня и игровой зоны
        if (dayMonth != null && gameZoneId != null) {
            //находим игровую зону
            Optional<GameZone> optionalGameZone = gameZoneRepository.findById(gameZoneId);
            //проверяем найдена ли игровая зона
            if (optionalGameZone.isPresent()) {
                //разделяем день и месяц
                String[] dayAndMonth = dayMonth.split("\\.");
                //создаем переменную даты и дня
                LocalDate localDate = LocalDate.of(
                        Year.now().getValue(),
                        Integer.parseInt(dayAndMonth[1]),
                        Integer.parseInt(dayAndMonth[0])
                );
                //получаем игровую зону
                GameZone gameZone = optionalGameZone.get();
                //Все временные промежутки для резервирования
                List<Integer> allTimeReserve = new ArrayList<>();
                //с какого часа начать инициализацию
                int startInitTimeReserve = 10;
                //в случае если выбирается резерв текущего дня отсекаются прошедшие часы
                if (localDate.getDayOfYear() == LocalDate.now().getDayOfYear()) {
                    startInitTimeReserve = LocalTime.now().getHour() + 1;
                }
                //инициализация
                for (int i = startInitTimeReserve; i < 24; i++) {
                    allTimeReserve.add(i);
                }
                //Получаем лист заказов в которых есть резерв и он на требуемую дату
                List<Order> orderList = new ArrayList<>(orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, localDate));
                //Перебирает каждый найденный заказ
                for (Order o : orderList) {
                    //получает начальный час резерва
                    int start = o.getReserveTime().getHour();
                    //получает конечный час резерва
                    int end = o.getEndReserve().getHour();
                    //удаляет из allTimeReserve зарезервированные часы от start до end
                    for (int i = start; i < end; i++) {
                        allTimeReserve.remove((Object) i);
                    }
                }
                //объявляет массив первое число час второе сколько можно зарезервировать от него
                Integer[][] availableReserve = new Integer[allTimeReserve.size()][2];
                //передает в массив все доступные резервы
                for (int i = 0; i < allTimeReserve.size(); i++) {
                    availableReserve[i][0] = allTimeReserve.get(i);
                }
                //указывает на возможные часы резерва
                for (int i = availableReserve.length - 1; i >= 0; i--) {
                    if (availableReserve.length - 1 == i) {
                        availableReserve[i][1] = 24 - availableReserve[i][0];
                    } else {
                        if (availableReserve[i][0] == availableReserve[i + 1][0] - 1) {
                            availableReserve[i][1] = 1 + availableReserve[i + 1][1];
                        } else {
                            availableReserve[i][1] = 1;
                        }
                    }
                }
                model.addAttribute("gameZoneId", gameZoneId);
                model.addAttribute("dayMonth", dayMonth);
                model.addAttribute("freeTimes", availableReserve);
            } else {
                return "redirect:/" + orderId + "/reserve";
            }
        }
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user", optionalEmployee);
        model.addAttribute("orderId", orderId);
        model.addAttribute("gameZones", gameZoneList);
        model.addAttribute("dayOfReserve", dayOfReserve);
        return "/order/reserve";
    }
    //УЖАСНЫЙ УРЛ АХТУНГ
    @GetMapping("/{id}/reserve/Add/{day}/{gameZoneId}/{freeTime}/{maxHour}")
    public String addReserve(@PathVariable("id") Long orderId,
                             @PathVariable("day") String dayOfMount,
                             @PathVariable("gameZoneId") Long gameZoneId,
                             @PathVariable("freeTime") int freeTime,
                             @PathVariable("maxHour") int maxHour,
                             @ModelAttribute(value = "hour") int hour) {
        //проверяем корректность переданных часов
        if (maxHour < hour || hour <= 0) {
            return "redirect:/order/" + orderId + "/reserve?gameZoneId=" + gameZoneId + "&day=" + dayOfMount;
        }
        //находим заказ
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        //проверяем заказ на null
        if (optionalOrder.isEmpty()) return "/order/orderNotFound";
        //передает в переменную заказ
        Order order = optionalOrder.get();
        //находим гейм зону
        Optional<GameZone> optionalGameZone = gameZoneRepository.findById(gameZoneId);
        //проверяем зону на null
        if (optionalGameZone.isEmpty()) return "redirect:/gameZoneNotFound";
        //передаем в переменную игровую зону
        GameZone gameZone = optionalGameZone.get();
        //создаем переменную даты
        LocalDate localDate;
        //разделяем на дату и месяц
        int day = Integer.parseInt(dayOfMount.split("\\.")[0]);
        int mount = Integer.parseInt(dayOfMount.split("\\.")[1]);
        //проверяем является ли заказ на след год если да ставит новый год
        if (day < 7 && mount == 1 && LocalDate.now().getDayOfMonth() > 20)
            localDate = LocalDate.of(Year.now().getValue() + 1, mount, day);
        else localDate = LocalDate.of(Year.now().getValue(), mount, day);
        //получает начало резерва
        LocalTime localTimeStart = LocalTime.of(freeTime, 0);
        //получает конец резерва
        LocalTime localTimeEnd = LocalTime.of(freeTime + hour, 0);
        //передача всех данных
        order.setGameZone(gameZone);
        order.setReserveDate(localDate);
        order.setReserveTime(localTimeStart);
        order.setEndReserve(localTimeEnd);
        orderRepository.save(order);
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/{id}")
    public String orderShow(@PathVariable("id") Long id,
                            @AuthenticationPrincipal Employee employeeAut,
                            Model model) {
        //находит заказ по ид
        Optional<Order> optionalOrder = orderRepository.findById(id);
        //если заказ null, то вызывается страничка ошибки
        if (optionalOrder.isEmpty()) return "order/orderNotFound";
        //если заказ найден присваивает переменной
        Order order = optionalOrder.get();
        //находит всех гостей этого заказа и помещает их в guestList
        List<Guest> guestList = guestRepository.findAllByOrder(order);
        //создает список товаров сделанных гостями этого заказа
        List<GuestCart> guestCartList = new ArrayList<>();
        //помещает все товары в список
        for (Guest g : guestList) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        //Высчитывание итоговой цены
        if (!order.getPayment()) {
            int total = 0;
            for (GuestCart gc : guestCartList) {
                Product product = gc.getProduct();
                total += (product.getPrice() * gc.getQuantity());
            }
            order.setTotal(total);
        }
        //создает переменную игровой зоный заказы
        Optional<GameZone> gameZone = Optional.ofNullable(order.getGameZone());
        //создает переменную менеджера
        Optional<Employee> employee = Optional.ofNullable(order.getManager());
        Optional<Employee> optionalEmployeeAut = Optional.ofNullable(employeeAut);
        //передает все в модель
        model.addAttribute("user", optionalEmployeeAut);
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal() / 10.0);
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        model.addAttribute("gameZone", gameZone);
        model.addAttribute("manager", employee);
        return "order/order";
    }

    @PostMapping("/{id}/add_guest")
    public String addGuest(@PathVariable("id") Long id) {
        //Создает гостя
        Guest guest = new Guest();
        //получает заказ по ид
        Order order = orderRepository.findById(id).get();
        //присваивает гостя к заказу
        guest.setOrder(order);
        //получает и инкрементирует номер текущего гостя
        long count = guestRepository.countByOrder(order);
        count++;
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
        //переадресовывает на заказ
        return "redirect:/order/" + order.getId();
    }

    @PostMapping("/{id}/payment")
    public String payment(@PathVariable("id") Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) return "redirect:/orderNotFound";
        Order order = optionalOrder.get();
        List<Guest> guestList = guestRepository.findAllByOrder(order);
        //создает список товаров сделанных гостями этого заказа
        List<GuestCart> guestCartList = new ArrayList<>();
        //помещает все товары в список
        for (Guest g : guestList) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        //Высчитывание итоговой цены
        int total = 0;
        for (GuestCart gc : guestCartList) {
            Product product = gc.getProduct();
            total += (product.getPrice() * gc.getQuantity());
        }
        order.setTotal(total);
        order.setPayment(true);
        orderRepository.save(order);
        return "redirect:/order/" + order.getId();
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        //создает заказ
        Order order = new Order();
        //устанавливает менеджера, который создал заказ
        order.setManager(employee);
        //устанавливает дату создания заказа
        order.setDate(LocalDate.now());
        order.setPayment(false);
        order.setTariff(2);
        //сохраняет в бд заказ
        order = orderRepository.save(order);
        //переадресовывает на заказ
        return "redirect:/order/" + order.getId();
    }
}
