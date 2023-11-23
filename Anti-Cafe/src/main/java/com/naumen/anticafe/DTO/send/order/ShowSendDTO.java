package com.naumen.anticafe.DTO.send.order;

import com.naumen.anticafe.DTO.object.GuestCartOrderDTO;
import com.naumen.anticafe.DTO.object.GuestOrderDTO;
import com.naumen.anticafe.domain.Guest;
import com.naumen.anticafe.domain.GuestCart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Getter
@Setter
public class ShowSendDTO {
    private List<GuestOrderDTO> guestList;
    private List<GuestCartOrderDTO> guestCartList;
    private Optional<Long> guestIdError;
    private Optional<String> guestMessageError;
    private String employee;
    private String employeeOrder;
    private Optional<String> gameZone;
    private String reserveDate;
    private String reserveTime;
    private String endReserve;
    private boolean taggedDelete;
    private boolean payment;
    private int total;
    private long orderId;
    private long gameZoneId;
    private boolean access;
}
