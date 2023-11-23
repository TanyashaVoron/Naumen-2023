package com.naumen.anticafe.scheduler;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.properties.SchedulerProperties;
import com.naumen.anticafe.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class Scheduler {
    private final OrderService orderService;
    private final SchedulerProperties schedulerProperties;
    @Autowired
    public Scheduler(OrderService orderService, SchedulerProperties schedulerProperties) {
        this.orderService = orderService;
        this.schedulerProperties = schedulerProperties;
    }

    @Scheduled(cron = "0 0 3 * * ?")
/*    @Scheduled(fixedRate = 15000)*/
    public void OrderMark() throws NotFoundException {
        LocalDate localDate = LocalDate.now();
        localDate.minusDays(schedulerProperties.getTaggedDeletion());
/*        localDate.minusDays(0);*/
        for (Order o : orderService.getOrderMarkDeletion(localDate))
            orderService.deleteOrderCascade(o);
    }
}
