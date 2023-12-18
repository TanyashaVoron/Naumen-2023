package com.naumen.anticafe.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("scheduler.order.lifetime")
@Setter
public class SchedulerPropertiesImpl implements SchedulerProperties {
    @Max(value = 30, message = "От 1 до 30")
    @Min(value = 1, message = "От 1 до 30")
    private int taggedDeletion;

    @Override
    public int getTaggedDeletion() {
        return taggedDeletion;
    }
}
