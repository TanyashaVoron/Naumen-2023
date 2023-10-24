package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.GuestCartRepository;
import com.naumen.anticafe.repository.GuestRepository;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GuestRepository guestRepository;
    private final GuestCartRepository guestCartRepository;
    private final GameZoneRepository gameZoneRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            GuestRepository guestRepository,
                            GuestCartRepository guestCartRepository,
                            GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.guestRepository = guestRepository;
        this.guestCartRepository = guestCartRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    public String createOrder(Employee employee){
        Order order = new Order();
        order.setManager(employee);
        order.setDate(LocalDate.now());
        order.setPayment(false);
        order = orderRepository.save(order);
        return "order/"+order.getId();
    }
    public String payment(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) return "redirect:/orderNotFound";
        Order order = optionalOrder.get();
        calculateTotal(order);
        //имитация оплаты
        order.setPayment(true);
        order = orderRepository.save(order);
        return "redirect:/order/" + orderId;
    }
    public String reserveShow(Long orderId,
                              Long gameZoneId,
                              String dayMonth,
                              Employee employee,
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
                showFreeTime(optionalGameZone.get(),dayMonth,model);
            } else {
                return "redirect:/" + orderId + "/reserve";
            }
        }
        Optional<Employee> optionalEmployee = Optional.ofNullable(employee);
        model.addAttribute("user", optionalEmployee);
        model.addAttribute("orderId", orderId);
        model.addAttribute("gameZones", gameZoneList);
        model.addAttribute("dayOfReserve", dayOfReserve);
        return "order/reserve";
    }
    public String addReserve(Long orderId, String dayOfMount, Long gameZoneId, int freeTime, int maxHour, String hours){
        //проверяем корректность переданных часов
        if(hours.equals("")){
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        int hour = Integer.parseInt(hours);
        if (maxHour < hour || hour <= 0) {
            return "redirect:/order/" + orderId + "/reserve?dayMonth=" + dayOfMount + "&gameZoneId=" + gameZoneId;
        }
        //находим заказ
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        //проверяем заказ на null
        if (optionalOrder.isEmpty()) return "order/orderNotFound";
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
        LocalTime localTimeEnd;
        localTimeEnd = LocalTime.of(freeTime + hour-1, 59);
        //передача всех данных
        order.setGameZone(gameZone);
        order.setReserveDate(localDate);
        order.setReserveTime(localTimeStart);
        order.setEndReserve(localTimeEnd);
        orderRepository.save(order);
        return "redirect:/order/" + orderId;
    }

    public String deleteGuest(Long orderId, Long guestId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()) return "redirect:/orderNotFound";
        Optional<Guest> optionalGuest = guestRepository.findById(guestId);
        if(optionalGuest.isEmpty()) return "redirect:/guestNotFound";
        long countGuestCart = guestCartRepository.countByGuest(optionalGuest.get());
        if(countGuestCart!=0) return "redirect:/order/"+orderId;
        guestRepository.delete(optionalGuest.get());
        return "redirect:/order/"+orderId;
    }
    public String reserveDelete(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()) return "redirect:/orderNotFound";
        Order order = optionalOrder.get();
        if(order.getPayment())return "redirect:/orderPayment";
        order.setGameZone(null);
        order.setReserveDate(null);
        order.setReserveTime(null);
        order.setEndReserve(null);
        orderRepository.save(order);
        return "redirect:/order/"+orderId;
    }

    public String orderShow(Long orderId,Employee employee, Model model){
        //находит заказ по ид
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
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
        Optional<Employee> optionalEmployeeAut = Optional.ofNullable(employee);
        //передает все в модель
        model.addAttribute("user", optionalEmployeeAut);
        model.addAttribute("order", order);
        model.addAttribute("total", order.getTotal() / 10.0);
        model.addAttribute("guests", guestList);
        model.addAttribute("products", guestCartList);
        return "order/order";
    }

    public String addGuest(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) return "redirect:/order/orderNotFound";
        Order order = optionalOrder.get();
        Guest guest = new Guest();
        guest.setOrder(order);
        //получает и инкриминирует номер текущего гостя
        long count = guestRepository.countByOrder(order);
        count++;
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
        return "redirect:/order/" + orderId;
    }
    private void deleteReserveTime(List<Integer> allTimeReserve,GameZone gameZone, LocalDate localDate){
        //Получаем лист заказов в которых есть резерв и он на требуемую дату
        List<Order> orderList = new ArrayList<>(orderRepository.findAllByGameZoneAndReserveDateOrderByReserveDate(gameZone, localDate));
        //Перебирает каждый найденный заказ
        for (Order o : orderList) {
            //получает начальный час резерва
            int start = o.getReserveTime().getHour();
            //получает конечный час резерва
            int end = o.getEndReserve().getHour();
            //удаляет из allTimeReserve зарезервированные часы от start до end
            for (int i = start; i <= end; i++) {
                allTimeReserve.remove((Object) i);
            }
        }
    }
    private Integer[][] getFreeTimeArray(List<Integer> allTimeReserve){
        //объявляет массив первое число час второе сколько можно зарезервировать от него
        Integer[][] availableReserve = new Integer[allTimeReserve.size()][2];
        //передает в массив все доступные резервы
        for (int i = 0; i < allTimeReserve.size(); i++) {
            availableReserve[i][0] = allTimeReserve.get(i);
        }
        return availableReserve;
    }
    private void giveTimeMaxHours(Integer[][]availableReserve){
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
    }
    private void showFreeTime(GameZone gameZone, String dayMonth, Model model){
        //разделяем день и месяц
        String[] dayAndMonth = dayMonth.split("\\.");
        //создаем переменную даты и дня
        LocalDate localDate = LocalDate.of(
                Year.now().getValue(),
                Integer.parseInt(dayAndMonth[1]),
                Integer.parseInt(dayAndMonth[0])
        );
        //Все временные промежутки для резервирования
        List<Integer> allTimeReserve = new ArrayList<>();
        //с какого часа начать инициализацию
        int startInitTimeReserve = 10;
        //в случае если выбирается резерв текущего дня отсекаются прошедшие часы
        if (localDate.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            startInitTimeReserve = Math.max(LocalTime.now().getHour() + 1,startInitTimeReserve);
        }
        //инициализация
        for (int i = startInitTimeReserve; i < 24; i++) {
            allTimeReserve.add(i);
        }
        //удаляет зарезервированные часы
        deleteReserveTime(allTimeReserve,gameZone,localDate);
        //получаем массив из списка свободных часов, где 0 время, а 1 максимальные часы
        Integer[][]availableReserve =  getFreeTimeArray(allTimeReserve);
        giveTimeMaxHours(availableReserve);

        model.addAttribute("gameZoneId", gameZone.getId());
        model.addAttribute("dayMonth", dayMonth);
        model.addAttribute("freeTimes", availableReserve);
    }
    private void calculateTotal(Order order){
        //список всех гостей
        List<Guest> guestList = guestRepository.findAllByOrder(order);
        //список всех товаров гостей
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
    }
}
