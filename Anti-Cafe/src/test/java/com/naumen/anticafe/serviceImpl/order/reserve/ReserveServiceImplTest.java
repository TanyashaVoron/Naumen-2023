package com.naumen.anticafe.serviceImpl.order.reserve;

import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.properties.ReserveServiceProperties;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.order.CalculationTotalService;
import com.naumen.anticafe.service.order.SearchOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReserveServiceImplTest {
    @InjectMocks
    private ReserveServiceImpl reserveService;
    @Mock
    private ReserveServiceProperties reserveServiceProperties;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CalculationTotalService calculateTotalService;
    @Mock
    private SearchOrderService searchOrderService;

    @Test
    void getAllDayOfReserve() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy");
        List<String> dayOfReserveList = Arrays.asList(
                        LocalDate.now().format(dtf),
                        LocalDate.now().plusDays(1).format(dtf),
                        LocalDate.now().plusDays(2).format(dtf),
                        LocalDate.now().plusDays(3).format(dtf),
                        LocalDate.now().plusDays(4).format(dtf),
                        LocalDate.now().plusDays(5).format(dtf),
                        LocalDate.now().plusDays(6).format(dtf)
        );
        Mockito.when(reserveServiceProperties.getDaysToReserve()).thenReturn(7);
        Assertions.assertEquals(reserveService.getAllDayOfReserve(), dayOfReserveList);
    }

    @Test
    void setReserve() {
        Order order = new Order();
        LocalDate reserveDay = LocalDate.now();
        GameZone gameZone = new GameZone();
        int freeTime = 12;
        int hour = 5;
        reserveService.setReserve(order, reserveDay, gameZone, freeTime, hour);
        Assertions.assertTrue(() -> {
            Assertions.assertEquals(order.getGameZone(),gameZone);
            Assertions.assertEquals(order.getReserveTime(), LocalTime.of(freeTime, 0));
            Assertions.assertEquals(order.getEndReserve(),LocalTime.of(freeTime + hour - 1, 59));
            Assertions.assertEquals(order.getReserveDate(),reserveDay);
            return true;
        });
        // Проверка на true перед установкой

        Mockito.verify(calculateTotalService, Mockito.times(1)).calculateTotal(order);
    }

    @Test
    void checkReserve_false_freeTime_nowHourMinusOneHour_Hour_1_dayNow() {
        LocalDate date = LocalDate.now();
        GameZone gameZone = new GameZone();
        int freeTime = LocalTime.now().getHour()-1;
        int hour = 1;
        int open = 9;
        int close = 24;
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setReserveDate(date);
            order.setReserveTime(LocalTime.of(10+(5*i),0));
            order.setEndReserve(LocalTime.of(12+(5*i),59));
            orderList.add(order);
        }
        Mockito.when(reserveServiceProperties.getOpeningHour()).thenReturn(open);
        Mockito.when(reserveServiceProperties.getClosingHour()).thenReturn(close);
        Mockito.when(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, date)).thenReturn(orderList);
        Assertions.assertFalse(reserveService.checkReserve(date,freeTime,hour,gameZone));
    }
    @Test
    void checkReserve_true_freeTime_Dynamically_Hour_Dynamically_dayNow() {
        LocalDate date = LocalDate.now();
        GameZone gameZone = new GameZone();
        int open = 9;
        int close = 24;
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setReserveDate(date);
            order.setReserveTime(LocalTime.of(10+(5*i),0));
            order.setEndReserve(LocalTime.of(12+(5*i),59));
            orderList.add(order);
        }
        List<int[]> stepOne = Arrays.asList(
                new int[]{9,1},
                new int[]{13,2},
                new int[]{14,1},
                new int[]{18,2},
                new int[]{19,1},
                new int[]{23,1}
        );
        List<int[]> stepTwo = new ArrayList<>();
        int nowHour = LocalTime.now().getHour();
        for(int[] e:stepOne){
            if(e[0]>nowHour){
                stepTwo.add(e);
            }
        }
        Mockito.when(reserveServiceProperties.getOpeningHour()).thenReturn(open);
        Mockito.when(reserveServiceProperties.getClosingHour()).thenReturn(close);
        Mockito.when(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, date)).thenReturn(orderList);
        if(!stepTwo.isEmpty()) {
            Assertions.assertTrue(reserveService.checkReserve(date,stepTwo.get(0)[0],stepTwo.get(0)[1],gameZone));
        }else {
            Assertions.assertTrue(orderList.isEmpty());
        }
    }
    @Test
    void checkReserve_false_freeTime_13_Hour_1_nextDay() {
        LocalDate date = LocalDate.now().plusDays(1);
        GameZone gameZone = new GameZone();
        int freeTime = 13;
        int hour = 1;
        int open = 9;
        int close = 24;
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setReserveDate(date);
            order.setReserveTime(LocalTime.of(10+(5*i),0));
            order.setEndReserve(LocalTime.of(12+(5*i),59));
            orderList.add(order);
        }
        Mockito.when(reserveServiceProperties.getOpeningHour()).thenReturn(open);
        Mockito.when(reserveServiceProperties.getClosingHour()).thenReturn(close);
        Mockito.when(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, date)).thenReturn(orderList);
        Assertions.assertTrue(reserveService.checkReserve(date,freeTime,hour,gameZone));
    }

    @Test
    void deleteReserve() {
        Long orderId = 1L;
        reserveService.deleteReserve(orderId);
        Mockito.verify(orderRepository,Mockito.times(1)).deleteReserve(orderId);
    }

    @Test
    void getFreeTimesAndMaxHourReserve_open_9_close_24_nextDay() {
        LocalDate date = LocalDate.now().plusDays(1);
        GameZone gameZone = new GameZone();
        int open = 9;
        int close = 24;
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setReserveDate(date);
            order.setReserveTime(LocalTime.of(10+(5*i),0));
            order.setEndReserve(LocalTime.of(12+(5*i),59));
            orderList.add(order);
        }
        int[][] result = new int[][]{
                {9,1},
                {13,2},
                {14,1},
                {18,2},
                {19,1},
                {23,1}
        };
        Mockito.when(reserveServiceProperties.getOpeningHour()).thenReturn(open);
        Mockito.when(reserveServiceProperties.getClosingHour()).thenReturn(close);
        Mockito.when(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, date)).thenReturn(orderList);
        Assertions.assertArrayEquals(reserveService.getFreeTimesAndMaxHourReserve(gameZone,date),result);
    }
    @Test
    void getFreeTimesAndMaxHourReserve_open_9_close_24_nowDay() {
        LocalDate date = LocalDate.now();
        GameZone gameZone = new GameZone();
        int open = 9;
        int close = 24;
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setReserveDate(date);
            order.setReserveTime(LocalTime.of(10+(5*i),0));
            order.setEndReserve(LocalTime.of(12+(5*i),59));
            orderList.add(order);
        }
        List<int[]> stepOne = Arrays.asList(
                new int[]{9,1},
                new int[]{13,2},
                new int[]{14,1},
                new int[]{18,2},
                new int[]{19,1},
                new int[]{23,1}
        );
        List<int[]> stepTwo = new ArrayList<>();
        int nowHour = LocalTime.now().getHour();
        for(int[] e:stepOne){
            if(e[0]>nowHour){
                stepTwo.add(e);
            }
        }
        int[][] result = stepTwo.toArray(new int[0][]);
        Mockito.when(reserveServiceProperties.getOpeningHour()).thenReturn(open);
        Mockito.when(reserveServiceProperties.getClosingHour()).thenReturn(close);
        Mockito.when(searchOrderService.getOrderByGameZoneAndReserveDate(gameZone, date)).thenReturn(orderList);
        Assertions.assertArrayEquals(reserveService.getFreeTimesAndMaxHourReserve(gameZone,date),result);
    }
}