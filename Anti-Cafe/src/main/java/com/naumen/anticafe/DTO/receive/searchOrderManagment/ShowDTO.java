package com.naumen.anticafe.DTO.receive.searchOrderManagment;

import java.time.LocalDate;

/**
 * @param employeeId поиск по сотруднику
 */
public record ShowDTO(Long orderId,
                      Long gameZoneId,
                      Boolean payment,
                      LocalDate reserveDate,
                      Long employeeId,
                      Integer page) {
    public ShowDTO {
        if (page == null || page < 1) page = 1;
    }
}
