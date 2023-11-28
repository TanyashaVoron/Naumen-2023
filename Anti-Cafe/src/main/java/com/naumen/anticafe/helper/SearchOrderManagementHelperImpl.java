package com.naumen.anticafe.helper;

import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.EmployeeDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.GameZoneDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.OrderDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.order.SearchOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Component
public class SearchOrderManagementHelperImpl implements SearchOrderManagementHelper{
    private final EmployeeService employeeService;
    private final GameZoneService gameZoneService;
    private final SearchOrderService searchOrderService;
    @Autowired
    public SearchOrderManagementHelperImpl(EmployeeService employeeService,
                                            GameZoneService gameZoneService, SearchOrderService searchOrderService) {
        this.employeeService = employeeService;
        this.gameZoneService = gameZoneService;
        this.searchOrderService = searchOrderService;
    }
    public ShowSendDTO searchOrder(ShowDTO dto, boolean orderMarker,String employeeUsername) throws NotFoundException {
        Employee user =employeeService.searchEmployee(employeeUsername);
        List<EmployeeDTO> employeeList = new ArrayList<>();
        for (Employee e : employeeService.getEmployeeList(true))
            employeeList.add(new EmployeeDTO(e.getId(),e.getName()));
        List<GameZoneDTO> gameZoneList = new ArrayList<>();
        for (GameZone gz :gameZoneService.getGameZoneList())
            gameZoneList.add(new GameZoneDTO(gz.getId(),gz.getName()));
        GameZone gameZone = null;
        Employee employee = null;
        if (dto.gameZoneId() != null) gameZone = gameZoneService.getGameZone(dto.gameZoneId());
        if (dto.employeeSearch() != null) employee = employeeService.getEmployee(dto.employeeSearch());
        List<Order> orders = searchOrderService.getOrderByIdOrGameZoneOrPayment(
                dto.orderId(),
                gameZone,
                dto.payment(),
                dto.reserveDate(),
                employee,
                orderMarker
        );
        List<OrderDTO> orderList = new ArrayList<>();
        for(Order o : orders) {
            Optional<String> gameZoneName;
            String reserveDate;
            String reserveTime;
            String endReserve;
            if (o.getGameZone() == null) {
                gameZoneName = Optional.ofNullable(null);
                reserveDate = null;
                reserveTime = null;
                endReserve = null;
            } else {
                gameZoneName = Optional.of(o.getGameZone().getName());
                reserveDate = o.getReserveDate().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy"));
                reserveTime = o.getReserveTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                endReserve = o.getEndReserve().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            orderList.add(new OrderDTO(
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
        ShowSendDTO sendDTO = new ShowSendDTO(employeeList,user.getName(),orderList,gameZoneList);
        return sendDTO;
    }
}
