package com.naumen.anticafe.DTO.send.employee;

import java.util.List;

/**
 * @param first         являеться ли страница первой
 * @param last          являеться ли страница последней
 * @param number        номер страници
 * @param totalElements общее число элементов
 * @param totalPages    общее число страниц
 */
public record ShowSendDTO(String nameEmployee,
                          List<EmployeeSendDTO> employees,
                          int totalPages,
                          long totalElements,
                          boolean last,
                          boolean first,
                          int number,
                          String username) {
}
