package com.naumen.anticafe.DTO.send.searchOrderManagment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShowSendDTO {
    private List<EmployeeDTO> employeeList;
    private String user;
    private List<OrderDTO> orderList;
    private List<GameZoneDTO> gameZoneList;
}
