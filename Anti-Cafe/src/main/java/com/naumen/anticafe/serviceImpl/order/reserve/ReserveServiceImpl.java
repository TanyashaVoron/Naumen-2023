package com.naumen.anticafe.serviceImpl.order.reserve;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.properties.ReserveServiceProperties;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.CalculationTotalService;
import com.naumen.anticafe.service.order.SearchOrderService;
import com.naumen.anticafe.service.order.reserve.ReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReserveServiceImpl implements ReserveService {
    private final ReserveServiceProperties reserveServiceProperties;
    private final OrderRepository orderRepository;
    private final CalculationTotalService calculateTotalService;
    private final SearchOrderService searchOrderService;

    @Autowired
    public ReserveServiceImpl(ReserveServiceProperties reserveServiceProperties,
                              OrderRepository orderRepository,
                              CalculationTotalService calculateTotalService,
                              SearchOrderService searchOrderService) {
        this.reserveServiceProperties = reserveServiceProperties;
        this.orderRepository = orderRepository;
        this.calculateTotalService = calculateTotalService;
        this.searchOrderService = searchOrderService;
    }

    /**
     * Отдает все свободные дни для резерва
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDayOfReserve() {
        List<String> dayOfReserveList = new ArrayList<>();
        //создаем переменную текущего дня для заполнения
        LocalDate date = LocalDate.now();
        //формат даты
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy");
        //вносим в лист все последующее 7 дней
        for (int i = 0; i < reserveServiceProperties.getDaysToReserve(); i++) {
            LocalDate dateNow = date.plusDays(i);
            dayOfReserveList.add(dateNow.format(dtf));
        }
        return dayOfReserveList;
    }

    /**
     * Устанавливает резерв
     */
    @Override
    @Transactional
    public void setReserve(Order order,
                           LocalDate reserveDay,
                           GameZone gameZone,
                           int freeTime,
                           int hour) {
        //создаем переменную даты
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

    /**
     * Проверяем можно ли зарезервировать на переданный резерв
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkReserve(LocalDate date, int freeHour, int hour, GameZone gameZone) {
        int[][] time = getFreeTimesAndMaxHourReserve(gameZone, date);
        for (int[] t : time) {
            if (t[0] == freeHour && t[1] >= hour) {
                return true;
            }
        }
        return false;
    }

    /**
     * удаляет резерв
     */
    @Override
    @Transactional
    public void deleteReserve(Long orderId) {
        orderRepository.deleteReserve(orderId);
    }

    /**
     * передает все свободные часы с их макс временем резерва
     */
    @Override
    @Transactional(readOnly = true)
    public int[][] getFreeTimesAndMaxHourReserve(GameZone gameZone, LocalDate dayReserve) {
        //Все временные промежутки для резервирования
        List<Integer> allTimeReserve = getAllTimeReserve(dayReserve);
        //удаляет зарезервированные часы
        deleteReserveTime(allTimeReserve, gameZone, dayReserve);
        //получаем массив из списка свободных часов, где 0 время, а 1 максимальные часы
        int[][] availableReserve = getFreeTimeArray(allTimeReserve);
        giveTimeMaxHours(availableReserve);
        return availableReserve;
    }

    /**
     * устанавливает в массив макс свободные часы для резерва
     */
    private void giveTimeMaxHours(int[][] availableReserve) {
        //указывает на возможные часы резерва
        if (availableReserve.length == 0) return;
        availableReserve[availableReserve.length - 1][1] = 1;
        for (int i = availableReserve.length - 2; i >= 0; i--) {
            if (availableReserve[i][0] == availableReserve[i + 1][0] - 1) {
                availableReserve[i][1] = 1 + availableReserve[i + 1][1];
            } else {
                availableReserve[i][1] = 1;
            }
        }
    }

    /**
     * удаляет из листа все зарезервираваные часы
     */
    private void deleteReserveTime(List<Integer> allTimeReserve, GameZone gameZone, LocalDate localDate) {
        //Получаем лист заказов в которых есть резерв и он на требуемую дату
        List<Order> orderList = searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, localDate);
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

    /**
     * Создает массив из листа
     */
    private int[][] getFreeTimeArray(List<Integer> allTimeReserve) {
        //объявляет массив первое число час второе сколько можно зарезервировать от него
        int[][] availableReserve = new int[allTimeReserve.size()][2];
        //передает в массив все доступные резервы
        for (int i = 0; i < allTimeReserve.size(); i++) {
            availableReserve[i][0] = allTimeReserve.get(i);
        }
        return availableReserve;
    }

    /**
     * передает все возможные часы резерва
     */
    private List<Integer> getAllTimeReserve(LocalDate dayReserve) {
        List<Integer> allTimeReserve = new ArrayList<>();
        //с какого часа начать инициализацию
        int openingHour = reserveServiceProperties.getOpeningHour();
        //в случае если выбирается резерв текущего дня отсекаются прошедшие часы
        if (dayReserve.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            openingHour = Math.max(LocalTime.now().getHour() + 1, openingHour);
        }
        int closingHour = reserveServiceProperties.getClosingHour();
        //инициализация
        for (int i = openingHour; i < closingHour; i++) {
            allTimeReserve.add(i);
        }
        return allTimeReserve;
    }
}
