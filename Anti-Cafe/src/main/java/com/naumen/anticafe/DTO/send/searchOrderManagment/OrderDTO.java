package com.naumen.anticafe.DTO.send.searchOrderManagment;

import java.util.Optional;

public record OrderDTO(Long orderId,
                       Optional<String> gameZone,
                       String reserveDate,
                       String reserveTime,
                       String endReserve,
                       String employee,
                       boolean payment,
                       int total) {

}
