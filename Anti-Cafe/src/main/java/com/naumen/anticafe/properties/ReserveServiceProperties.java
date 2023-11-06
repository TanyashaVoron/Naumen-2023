package com.naumen.anticafe.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("reserve.service")
@Data
@Validated
public class ReserveServiceProperties {
    @Max(value = 30, message = "От 1 до 30")
    @Min(value = 1, message = "От 1 до 30")
    private int daysToReserve;
    @Max(value = 23, message = "От 0 до 23")
    @Min(value = 0, message = "От 0 до 23")
    private int openingHour;
    @Max(value = 24, message = "От 1 до 24")
    @Min(value = 1, message = "От 1 до 24")
    private int closingHour;

    @AssertTrue(message = "Время закрытия должно быть больше времени открытия")
    public boolean isValidOpeningClosingHours() {
        return openingHour < closingHour;
    }
}
