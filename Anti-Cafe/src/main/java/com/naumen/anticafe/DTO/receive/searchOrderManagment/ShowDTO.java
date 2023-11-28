package com.naumen.anticafe.DTO.receive.searchOrderManagment;

import java.time.LocalDate;

public record ShowDTO(Long orderId,
                      Long gameZoneId,
                      Boolean payment,
                      LocalDate reserveDate,
                      Long employeeSearch) {
}
