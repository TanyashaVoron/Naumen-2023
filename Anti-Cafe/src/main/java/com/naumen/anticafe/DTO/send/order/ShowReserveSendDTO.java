package com.naumen.anticafe.DTO.send.order;

import java.util.List;
import java.util.Optional;

public record ShowReserveSendDTO(String employee,
                                 long orderId,
                                 List<GameZoneReserveDTO> gameZones,
                                 List<String> dayOfReserve,
                                 Long gameZoneId,
                                 String day,
                                 Optional<int[][]> freeTimes,
                                 Optional<Integer> hourError,
                                 String hourMessageError) {
}
