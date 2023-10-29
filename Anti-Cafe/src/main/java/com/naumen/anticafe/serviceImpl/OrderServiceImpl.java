package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.GuestsHaveGoodsException;
import com.naumen.anticafe.error.NotFoundException;
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
    @Override
    public Integer[][] getFreeTimesAndMaxHourReserve(Long gameZoneId, String dayMonth) throws NotFoundException {
        GameZone gameZone = getGameZone(gameZoneId);
        //разделяем день и месяц
        String[] dayAndMonth = dayMonth.split("\\.");
        //создаем переменную даты и дня
        LocalDate dayReserve = LocalDate.of(
                Year.now().getValue(),
                Integer.parseInt(dayAndMonth[1]),
                Integer.parseInt(dayAndMonth[0])
        );
        //Все временные промежутки для резервирования
        List<Integer> allTimeReserve = getAllTimeReserve(dayReserve);
        //удаляет зарезервированные часы
        deleteReserveTime(allTimeReserve,gameZone,dayReserve);
        //получаем массив из списка свободных часов, где 0 время, а 1 максимальные часы
        Integer[][]availableReserve =  getFreeTimeArray(allTimeReserve);
        giveTimeMaxHours(availableReserve);
        return availableReserve;
    }
    @Override
    public List<GameZone> getGameZoneList(){
        return gameZoneRepository.findAll();
    }

    @Override
    public List<String> getAllDayOfReserve() {
        List<String> dayOfReserveList =new ArrayList<>();
        //создаем переменную текущего дня для заполнения
        LocalDate date = LocalDate.now();
        //вносим в лист все последующее 7 дней
        for (int i = 0; i < 7; i++) {
            LocalDate dateNow = date.plusDays(i);
            dayOfReserveList.add(dateNow.getDayOfMonth() + "." + dateNow.getMonthValue());
        }
        return dayOfReserveList;
    }
    @Override
    public Order setReserve(Long orderId,
                            String dayOfMount,
                            Long gameZoneId,
                            int freeTime,
                            int maxHour,
                            int hour) throws NotFoundException {

        Order order = getOrder(orderId);
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
        GameZone gameZone = getGameZone(gameZoneId);
        //создаем переменную даты
        LocalDate reserveDay = getReserveDay(dayOfMount);
        //получает начало резерва
        LocalTime reserveStart = LocalTime.of(freeTime, 0);
        //получает конец резерва
        LocalTime reserveEnd = LocalTime.of(freeTime + hour-1, 59);
        //передача всех данных
        order.setGameZone(gameZone);
        order.setReserveDate(reserveDay);
        order.setReserveTime(reserveStart);
        order.setEndReserve(reserveEnd);
        calculateTotal(order);
        orderRepository.save(order);
        return order;
    }
    @Override
    public List<GuestCart> getGuestCartListByGuest(List<Guest> guestList){
        List<GuestCart> guestCartList = new ArrayList<>();
        for (Guest g : guestList) {
            guestCartList.addAll(guestCartRepository.findAllByGuest(g));
        }
        return guestCartList;
    }
    @Override
    public List<Guest> getGuestListByOrder(Order order){
        return guestRepository.findAllByCompositeIdOrder(order);
    }






    @Override
    public Order getOrder(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        return optionalOrder.get();
    }
    @Override
    public Order deleteGuest(Long orderId, Long guestId) throws NotFoundException, GuestsHaveGoodsException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        Order order = optionalOrder.get();
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
        Optional<Guest> optionalGuest = guestRepository.findById(new GuestId(guestId, order));
        if(optionalGuest.isEmpty()) throw new NotFoundException("Гость не найден");
        long countGuestCart = guestCartRepository.countByGuest(optionalGuest.get());
        if(countGuestCart!=0) throw new GuestsHaveGoodsException("У гостя есть товары",guestId);
        guestRepository.delete(optionalGuest.get());
        return order;
    }
    @Override
    public Order deleteReserve(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        Order order = optionalOrder.get();
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
        order.setGameZone(null);
        order.setReserveDate(null);
        order.setReserveTime(null);
        order.setEndReserve(null);
        orderRepository.save(order);
        return order;
    }
    @Override
    public Order addGuest(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        Order order = optionalOrder.get();
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
        Guest guest = new Guest();
        //получает и инкриминирует номер текущего гостя
        long count = guestRepository.countByCompositeIdOrder(order);
        count++;
        guest.setCompositeId(new GuestId(count, order));
        //создает имя гостя
        guest.setName("Гость №" + count);
        //сохраняет гостя
        guestRepository.save(guest);
        return order;
    }
    @Override
    public Order payment(Long orderId) throws NotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Заказ не найден");
        Order order = optionalOrder.get();
        if(order.getPayment()) throw new NotFoundException("Заказ уже оплачен");
        calculateTotal(order);
        //имитация оплаты
        order.setPayment(true);
        orderRepository.save(order);
        return order;
    }
    @Override
    public Order createOrder(Employee employee){
        Order order = new Order();
        order.setManager(employee);
        order.setDate(LocalDate.now());
        order.setPayment(false);
        orderRepository.save(order);
        return order;
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




    private void calculateTotal(Order order){
        //список всех гостей
        List<Guest> guestList = getGuestListByOrder(order);
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
    private List<Integer> getAllTimeReserve(LocalDate dayReserve) {
        List<Integer> allTimeReserve = new ArrayList<>();
        //с какого часа начать инициализацию
        int startInitTimeReserve = 10;
        //в случае если выбирается резерв текущего дня отсекаются прошедшие часы
        if (dayReserve.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            startInitTimeReserve = Math.max(LocalTime.now().getHour() + 1,startInitTimeReserve);
        }
        //инициализация
        for (int i = startInitTimeReserve; i < 24; i++) {
            allTimeReserve.add(i);
        }
        return allTimeReserve;
    }

    private LocalDate getReserveDay(String dayOfMount) {
        LocalDate reserveDay;
        //разделяем на дату и месяц
        int day = Integer.parseInt(dayOfMount.split("\\.")[0]);
        int mount = Integer.parseInt(dayOfMount.split("\\.")[1]);
        //проверяем является ли заказ на след год если да ставит новый год
        if (day < 7 && mount == 1 && LocalDate.now().getDayOfMonth() > 20)
            reserveDay = LocalDate.of(Year.now().getValue() + 1, mount, day);
        else reserveDay = LocalDate.of(Year.now().getValue(), mount, day);
        return reserveDay;
    }

    private GameZone getGameZone(Long gameZoneId) throws NotFoundException {
        Optional<GameZone> optionalOrder = gameZoneRepository.findById(gameZoneId);
        if (optionalOrder.isEmpty()) throw new NotFoundException("Игровая зона не найдена");
        return optionalOrder.get();
    }

}
