package com.naumen.anticafe.DTO.send.employee;

import java.util.List;

public record ShowSendDTO(String nameEmployee,
                          List<EmployeeSendDTO> employees) {
}
