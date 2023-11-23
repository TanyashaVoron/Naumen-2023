package com.naumen.anticafe.DTO.send.order;

import com.naumen.anticafe.DTO.object.GameZoneReserveDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ShowReserveSendDTO {
    private String employee;
    private long orderId;
    private List<GameZoneReserveDTO> gameZones;
    private List<String> dayOfReserve;
    private long gameZoneId;
    private String dayMonth;
    private Optional<int[][]> freeTimes;
    private Optional<Integer> hourError;
    private String hourMessageError;


    public ShowReserveSendDTO(String employee,
                              long orderId,
                              List<GameZoneReserveDTO> gameZones,
                              List<String> dayOfReserve,
                              Optional<int[][]> freeTimes,
                              Optional<Integer> hourError,
                              String hourMessageError) {
        this.employee = employee;
        this.orderId = orderId;
        this.gameZones = gameZones;
        this.dayOfReserve = dayOfReserve;
        this.freeTimes = freeTimes;
        this.hourError = hourError;
        this.hourMessageError=hourMessageError;
    }
}
