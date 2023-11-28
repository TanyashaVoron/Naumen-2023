package com.naumen.anticafe.DTO.receive.order;

public record ShowReserveDTO(Long gameZoneId,
                             String dayMonth,
                             String hourMessageError,
                             Integer hourError) {

}
