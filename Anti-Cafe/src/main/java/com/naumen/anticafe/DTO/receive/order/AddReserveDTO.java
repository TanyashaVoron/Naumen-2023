package com.naumen.anticafe.DTO.receive.order;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @param day      день резерва
 * @param freeTime выбраное время резерва
 * @param hour     количество часов для резервирования
 * @param maxHour  максимально возможное hour
 */
public record AddReserveDTO(String day,
                            Long gameZoneId,
                            int freeTime,
                            int maxHour,
                            @Min(value = 1, message = "Минимальное количество часов 1")
                            @NotNull(message = "Не может быть пустым")
                            Integer hour) {

    @AssertTrue(message = "Слишком большое число")
    public boolean isHourValid() {
        return hour <= maxHour;
    }
}
