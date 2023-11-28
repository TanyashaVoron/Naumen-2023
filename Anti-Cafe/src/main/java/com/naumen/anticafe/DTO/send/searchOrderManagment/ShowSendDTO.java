package com.naumen.anticafe.DTO.send.searchOrderManagment;

import java.util.List;

public record ShowSendDTO(List<EmployeeDTO> employeeList,
                          String user,
                          List<OrderDTO> orderList,
                          List<GameZoneDTO> gameZoneList) {

}
