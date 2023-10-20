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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    private OrderRepository orderRepository;
    private GuestRepository guestRepository;
    private GuestCartRepository guestCartRepository;
    private GameZoneRepository gameZoneRepository;

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
                              @RequestParam(value = "gameZoneId",required = false) Long gameZoneId,
                              @RequestParam(value = "day",required = false) String day,
                              Model model){
        List<GameZone> gameZoneList = new ArrayList<>(gameZoneRepository.findAll());
        List<String> dayOfReserve = new ArrayList<>();
        LocalDate dateNow = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            dateNow = dateNow.plusDays(1);
            dayOfReserve.add(dateNow.getDayOfMonth()+"."+dateNow.getMonthValue());
        }
        if(day!=null&&gameZoneId!=null){
            Optional<GameZone> optionalGameZone = gameZoneRepository.findById(gameZoneId);
            String[] dayAndMonth = day.split("\\.");
            LocalDate localDate = LocalDate.of(
                    Year.now().getValue(),
                    Integer.parseInt(dayAndMonth[1]),
                    Integer.parseInt(dayAndMonth[0])
            );
            if(optionalGameZone.isPresent()) {
                GameZone gameZone = optionalGameZone.get();
                List<Integer> time = new ArrayList<>();
                for (int i = 10; i < 24; i++) {
                    time.add(i);
                }
                List<Order> orderList = new ArrayList<>(orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone,localDate));
                for(Order o :orderList){
                    LocalTime start = o.getReserveTime();
                    LocalTime end = o.getEndReserve();
                    while (!start.equals(end)){
                        time.remove((Object)start.getHour());
                        start=start.plusHours(1);
                    }
                    time.remove((Object)end.getHour());
                }
                Integer[][] freeTime = new Integer[time.size()][2];
                for (int i = 0; i < time.size(); i++) {
                    freeTime[i][0]=time.get(i);
                }
                int left = 0;
                for (int i = 0; i < freeTime.length; i++) {
                    if(i==freeTime.length-1){
                        freeTime[i][1]=24-freeTime[i][0];
                        int t = 1;
                        for (int j = i; j >= left; j--) {
                            freeTime[j][1] = t++;
                        }
                    }else {
                        if (freeTime[i][0]!=(freeTime[i + 1][0] - 1)){
                            int t = 1;
                            for (int j = i; j >= left; j--) {
                                freeTime[j][1] = t++;
                            }
                            left = i + 1;
                        }
                    }

                }
                model.addAttribute("gameZoneId",gameZoneId);
                model.addAttribute("day",day);
                model.addAttribute("freeTimes",freeTime);
            }
        }
        model.addAttribute("orderId",orderId);
        model.addAttribute("gameZones",gameZoneList);
        model.addAttribute("dayOfReserve",dayOfReserve);
        return "reserve";
    }
    @GetMapping("/{id}/reserve/Add/{day}/{gameZoneId}/{freeTime}/{maxHour}")
    public String addReserve(@PathVariable("id")Long orderId,
                             @PathVariable("day")String dayOfMount,
                             @PathVariable("gameZoneId")Long gameZoneId,
                             @PathVariable("freeTime")int freeTime,
                             @PathVariable("maxHour") int maxHour,
                             @ModelAttribute(value = "hour") int hour){
        if(maxHour<hour||hour<=0){
            return "redirect:/order/"+orderId+"/reserve?gameZoneId="+gameZoneId+"&day="+dayOfMount;
        }
        Order order = orderRepository.findById(orderId).get();
        GameZone gameZone = gameZoneRepository.findById(gameZoneId).get();
        LocalDate localDate;
        int day = Integer.parseInt(dayOfMount.split("\\.")[0]);
        int mount = Integer.parseInt(dayOfMount.split("\\.")[1]);
        if(day<7&&mount==1) localDate = LocalDate.of(Year.now().getValue()+1,mount,day);
        else localDate = LocalDate.of(Year.now().getValue(),mount,day);
        LocalTime localTimeStart = LocalTime.of(freeTime,0);
        LocalTime localTimeEnd = LocalTime.of(freeTime+hour,0);
        order.setGameZone(gameZone);
        order.setReserveDate(localDate);
        order.setReserveTime(localTimeStart);
        order.setEndReserve(localTimeEnd);
        orderRepository.save(order);
        return "redirect:/order/"+orderId;
    }
    @GetMapping("/{id}")
    public String orderShow(@PathVariable("id") Long id,
                            Model model) {
        //находит заказ по ид
        Optional<Order> optionalOrder = orderRepository.findById(id);
        //если заказ null то вызывается страничка ошибки
        if (optionalOrder.isEmpty()) return "orderNotFound";
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
        //создает переменную игровой зоный заказы
        Optional<GameZone> gameZone = Optional.ofNullable(order.getGameZone());
        //создает переменную менеджера
        Optional<Employee> employee = Optional.ofNullable(order.getManager());
        //передает все в модель
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal());
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        model.addAttribute("gameZone", gameZone);
        model.addAttribute("manager", employee);
        return "order";
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

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal Employee employee) {
        //создает заказ
        Order order = new Order();
        //устанавливает менеджера, который создал заказ
        order.setManager(employee);
        //устанавливает дату создания заказа
        order.setDate(LocalDate.now());
        //сохраняет в бд заказ
        order = orderRepository.save(order);
        //переадрисовывает на заказ
        return "redirect:/order/" + order.getId();
    }
}
