package com.naumen.anticafe.serviceImpl.order.reserve;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.properties.ReserveServiceProperties;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.order.CalculationTotalService;
import com.naumen.anticafe.service.order.PaymentOrderService;
import com.naumen.anticafe.service.order.SearchOrderService;
import com.naumen.anticafe.service.order.reserve.ReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReserveServiceImpl implements ReserveService {
    private final GameZoneService gameZoneService;
    private final ReserveServiceProperties reserveServiceProperties;
    private final PaymentOrderService paymentOrderService;
    private final CalculationTotalService calculateTotalService;
    private final SearchOrderService searchOrderService;

    @Autowired
    public ReserveServiceImpl(GameZoneService gameZoneService, ReserveServiceProperties reserveServiceProperties, PaymentOrderService paymentOrderService, CalculationTotalService calculateTotalService, SearchOrderService searchOrderService) {
        this.gameZoneService = gameZoneService;
        this.reserveServiceProperties = reserveServiceProperties;
        this.paymentOrderService = paymentOrderService;
        this.calculateTotalService = calculateTotalService;
        this.searchOrderService = searchOrderService;
    }
    @Override
    public List<String> getAllDayOfReserve() {
        List<String> dayOfReserveList = new ArrayList<>();
        //создаем переменную текущего дня для заполнения
        LocalDate date = LocalDate.now();
        //вносим в лист все последующее 7 дней
        for (int i = 0; i < reserveServiceProperties.getDaysToReserve(); i++) {
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
        paymentOrderService.checkPaymentOrder(order);
        GameZone gameZone = gameZoneService.getGameZone(gameZoneId);
        //создаем переменную даты
        LocalDate reserveDay = getReserveDay(dayOfMount);
        //получает начало резерва
        LocalTime reserveStart = LocalTime.of(freeTime, 0);
        //получает конец резерва
        LocalTime reserveEnd = LocalTime.of(freeTime + hour - 1, 59);
        //передача всех данных
        order.setGameZone(gameZone);
        order.setReserveDate(reserveDay);
        order.setReserveTime(reserveStart);
        order.setEndReserve(reserveEnd);
        calculateTotalService.calculateTotal(order);
    }

    @Override
    public void deleteReserve(Order order) throws NotFoundException {
        paymentOrderService.checkPaymentOrder(order);
        order.setGameZone(null);
        order.setReserveDate(null);
        order.setReserveTime(null);
        order.setEndReserve(null);
    }


    @Override
    public int[][] getFreeTimesAndMaxHourReserve(GameZone gameZone, String dayMonth){
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
        deleteReserveTime(allTimeReserve, gameZone, dayReserve);
        //получаем массив из списка свободных часов, где 0 время, а 1 максимальные часы
        int[][] availableReserve = getFreeTimeArray(allTimeReserve);
        giveTimeMaxHours(availableReserve);
        return availableReserve;
    }

    private void giveTimeMaxHours(int[][] availableReserve) {
        //указывает на возможные часы резерва
        int closingHour = reserveServiceProperties.getClosingHour();
        for (int i = availableReserve.length - 1; i >= 0; i--) {
            if (availableReserve.length - 1 == i) {
                availableReserve[i][1] = closingHour - availableReserve[i][0];
            } else {
                if (availableReserve[i][0] == availableReserve[i + 1][0] - 1) {
                    availableReserve[i][1] = 1 + availableReserve[i + 1][1];
                } else {
                    availableReserve[i][1] = 1;
                }
            }
        }
    }

    private void deleteReserveTime(List<Integer> allTimeReserve, GameZone gameZone, LocalDate localDate) {
        //Получаем лист заказов в которых есть резерв и он на требуемую дату
        List<Order> orderList = new ArrayList<>(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, localDate));
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

    private int[][] getFreeTimeArray(List<Integer> allTimeReserve) {
        //объявляет массив первое число час второе сколько можно зарезервировать от него
        int[][] availableReserve = new int[allTimeReserve.size()][2];
        //передает в массив все доступные резервы
        for (int i = 0; i < allTimeReserve.size(); i++) {
            availableReserve[i][0] = allTimeReserve.get(i);
        }
        return availableReserve;
    }

    private List<Integer> getAllTimeReserve(LocalDate dayReserve) {
        List<Integer> allTimeReserve = new ArrayList<>();
        //с какого часа начать инициализацию
        int openingHour = reserveServiceProperties.getOpeningHour();
        //в случае если выбирается резерв текущего дня отсекаются прошедшие часы
        if (dayReserve.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            openingHour  = Math.max(LocalTime.now().getHour() + 1, openingHour );
        }
        int closingHour = reserveServiceProperties.getClosingHour();
        //инициализация
        for (int i = openingHour ; i < closingHour; i++) {
            allTimeReserve.add(i);
        }
        return allTimeReserve;
    }

    private LocalDate getReserveDay(String dayOfMount) {
        //разделяем на дату и месяц
        int day = Integer.parseInt(dayOfMount.split("\\.")[0]);
        int mount = Integer.parseInt(dayOfMount.split("\\.")[1]);
        //проверяем является ли заказ на след год если да ставит новый год
        LocalDate reserveDay;
        if (day < 7 && mount == 1 && LocalDate.now().getDayOfMonth() > 20)
            reserveDay = LocalDate.of(Year.now().getValue() + 1, mount, day);
        else reserveDay = LocalDate.of(Year.now().getValue(), mount, day);
        return reserveDay;
    }
}
