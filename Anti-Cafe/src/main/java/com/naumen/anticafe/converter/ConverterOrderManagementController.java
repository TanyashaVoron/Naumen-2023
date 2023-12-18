package com.naumen.anticafe.converter;

import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.EmployeeDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.GameZoneDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.OrderDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConverterOrderManagementController {
    public static ShowSendDTO convertToShowSendDTO(String username, List<GameZone> gameZoneList, List<Employee> employeeList, Page<Order> orderPage, ShowDTO dto) {
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for (Employee e : employeeList)
            employeeDTOList.add(new EmployeeDTO(e.getId(), e.getName()));
        List<GameZoneDTO> gameZoneDTOList = new ArrayList<>();
        for (GameZone gz : gameZoneList)
            gameZoneDTOList.add(new GameZoneDTO(gz.getId(), gz.getName()));
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order o : orderPage) {
            Optional<String> gameZoneName;
            String reserveDate;
            String reserveTime;
            String endReserve;
            if (o.getGameZone() == null) {
                gameZoneName = Optional.empty();
                reserveDate = null;
                reserveTime = null;
                endReserve = null;
            } else {
                gameZoneName = Optional.of(o.getGameZone().getName());
                reserveDate = o.getReserveDate().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"));
                reserveTime = o.getReserveTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                endReserve = o.getEndReserve().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            orderDTOList.add(new OrderDTO(
                    o.getId(),
                    gameZoneName,
                    reserveDate,
                    reserveTime,
                    endReserve,
                    o.getManager().getName(),
                    o.getPayment(),
                    o.getTotal()
            ));
        }
        return new ShowSendDTO(employeeDTOList, username, orderDTOList, gameZoneDTOList,
                orderPage.getTotalPages(),
                orderPage.getTotalElements(),
                orderPage.isLast(),
                orderPage.isFirst(),
                orderPage.getNumber(),
                dto.orderId(),
                dto.gameZoneId(),
                dto.payment(),
                dto.reserveDate(),
                dto.employeeId()
        );
    }
}
