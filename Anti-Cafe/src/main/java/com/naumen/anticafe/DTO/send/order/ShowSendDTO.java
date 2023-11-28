package com.naumen.anticafe.DTO.send.order;

import java.util.List;
import java.util.Optional;

public record ShowSendDTO(List<GuestOrderDTO> guestList,
                          List<GuestCartOrderDTO> guestCartList,
                          Optional<Long> guestIdError,
                          Optional<String> guestMessageError,
                          String employee,
                          String employeeOrder,
                          Optional<String> gameZone,
                          String reserveDate,
                          String reserveTime,
                          String endReserve,
                          boolean taggedDelete,
                          boolean payment,
                          int total,
                          long orderId,
                          long gameZoneId,
                          boolean access) {

}
