package com.naumen.anticafe.DTO.receive.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShowReserveDTO {
    private Long gameZoneId;
    private String dayMonth;
    private String hourMessageError;
    private Integer hourError;
}
