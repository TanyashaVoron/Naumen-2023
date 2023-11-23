package com.naumen.anticafe.DTO.receive.order;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
@Getter
@Setter
@AllArgsConstructor
public class AddReserveDTO {
    private String dayOfMount;
    private Long gameZoneId;
    private int freeTime;
    private int maxHour;
    @Min(value = 1,message = "Минимальное количество часов 1")
    @NotNull(message = "Не может быть пустым")
    private Integer hour;
    @AssertTrue(message = "Слишком большое число")
    public boolean isHourValid() {
        return hour <= maxHour;
    }
}
