package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.GameZoneService;
import com.naumen.anticafe.service.GuestService;
import com.naumen.anticafe.service.OrderService;
import com.naumen.anticafe.service.ReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class ReserveServiceImpl implements ReserveService {
    private final OrderService orderService;
    private final GameZoneService gameZoneService;
    @Autowired
    public ReserveServiceImpl(OrderService orderService,
                              GameZoneService gameZoneService) {
        this.orderService = orderService;
        this.gameZoneService = gameZoneService;
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
    public void setReserve(Order order,
                            String dayOfMount,
                            Long gameZoneId,
                            int freeTime,
                            int maxHour,
                            int hour) throws NotFoundException {
        orderService.checkPaymentOrder(order);
        GameZone gameZone = gameZoneService.getGameZone(gameZoneId);
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
        orderService.calculateTotal(order);
        orderService.save(order);
    }
    @Override
    public void deleteReserve(Order order) throws NotFoundException {
        orderService.checkPaymentOrder(order);
        order.setGameZone(null);
        order.setReserveDate(null);
        order.setReserveTime(null);
        order.setEndReserve(null);
        orderService.save(order);
    }


    @Override
    public Integer[][] getFreeTimesAndMaxHourReserve(GameZone gameZone, String dayMonth) throws NotFoundException {
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
        List<Order> orderList = new ArrayList<>(orderService.getOrderByGameZoneAndReserveDate(gameZone,localDate));
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
}
