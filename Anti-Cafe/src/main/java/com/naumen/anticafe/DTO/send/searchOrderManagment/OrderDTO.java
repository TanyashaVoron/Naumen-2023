package com.naumen.anticafe.DTO.send.searchOrderManagment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Optional<String> GameZone;
    private String reserveDate;
    private String reserveTime;
    private String endReserve;
    private String employee;
    private boolean payment;
    private int total;
}
