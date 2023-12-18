package com.naumen.anticafe.scheduler;

import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.properties.SchedulerProperties;
import com.naumen.anticafe.service.order.MarkDeletionOrderService;
import com.naumen.anticafe.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
public class Scheduler {
    private final OrderService orderService;
    private final MarkDeletionOrderService markDeletionOrderService;
    private final SchedulerProperties schedulerProperties;

    @Autowired
    public Scheduler(OrderService orderService, MarkDeletionOrderService markDeletionOrderService, SchedulerProperties schedulerProperties) {
        this.orderService = orderService;
        this.markDeletionOrderService = markDeletionOrderService;
        this.schedulerProperties = schedulerProperties;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void OrderMark() {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(schedulerProperties.getTaggedDeletion());
        for (Order o : markDeletionOrderService.getOrderMarkDeletion(localDate))
            orderService.deleteOrderCascade(o.getId());
    }
}
