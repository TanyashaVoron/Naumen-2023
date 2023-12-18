package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.converter.ConverterOrderManagementController;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.properties.PageProperties;
import com.naumen.anticafe.service.Employee.EmployeeService;
import com.naumen.anticafe.service.GameZone.GameZoneService;
import com.naumen.anticafe.service.order.SearchOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    private final EmployeeService employeeService;
    private final GameZoneService gameZoneService;
    private final SearchOrderService searchOrderService;
    private final PageProperties pageProperties;

    @Autowired
    public SearchController(EmployeeService employeeService,
                            GameZoneService gameZoneService,
                            SearchOrderService searchOrderService,
                            PageProperties pageProperties) {
        this.employeeService = employeeService;
        this.gameZoneService = gameZoneService;
        this.searchOrderService = searchOrderService;
        this.pageProperties = pageProperties;
    }
    /** отображение поиска не удаленных заказов*/
    @GetMapping()
    @Transactional(readOnly = true)
    public String showSearch(Model model,
                             @ModelAttribute ShowDTO dto,
                             @AuthenticationPrincipal(expression = "name") String employeeName) {
        Pageable pageable = PageRequest.of(dto.page() - 1, pageProperties.getPageSize());
        List<Employee> employeeList = employeeService.getEmployeeList(true);
        List<GameZone> gameZoneList = gameZoneService.getGameZoneList();
        Page<Order> orderList = searchOrderService.getOrderByIdOrGameZoneOrPayment(
                dto.orderId(),
                dto.gameZoneId(),
                dto.payment(),
                dto.reserveDate(),
                dto.employeeId(),
                false,
                pageable
        );
        ShowSendDTO sendDTO =
                ConverterOrderManagementController.convertToShowSendDTO(
                        employeeName,
                        gameZoneList,
                        employeeList,
                        orderList,
                        dto
                );
        model.addAttribute("sendDTO", sendDTO);
        return "search";
    }
}
