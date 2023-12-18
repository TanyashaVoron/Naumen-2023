package com.naumen.anticafe.DTO.receive.order;

/**
 * @param day день резерва
 */
public record ShowReserveDTO(Long gameZoneId,
                             String day,
                             String hourMessageError,
                             Integer hourError) {

}
