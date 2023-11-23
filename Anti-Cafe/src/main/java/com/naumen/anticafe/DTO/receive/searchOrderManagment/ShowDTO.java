package com.naumen.anticafe.DTO.receive.searchOrderManagment;

import com.naumen.anticafe.domain.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
@Getter
@Setter
public class ShowDTO {
    private Long orderId;
    private Long gameZoneId;
    private Boolean payment;
    private LocalDate reserveDate;
    private Long employeeSearch;
}
