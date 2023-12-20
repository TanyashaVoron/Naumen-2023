package com.naumen.anticafe.DTO.send.searchOrderManagment;

import java.time.LocalDate;
import java.util.List;

/**
 * @param first         являеться ли страница первой
 * @param last          являеться ли страница последней
 * @param number        номер страници
 * @param totalElements общее число элементов
 * @param totalPages    общее число страниц
 */
public record ShowSendDTO(List<EmployeeDTO> employeeList,
                          String user,
                          List<OrderDTO> orderList,
                          List<GameZoneDTO> gameZoneList,
                          int totalPages,
                          long totalElements,
                          boolean last,
                          boolean first,
                          int number,
                          Long orderId,
                          Long gameZoneId,
                          Boolean payment,
                          LocalDate reserveDate,
                          Long employeeSearch) {
}
