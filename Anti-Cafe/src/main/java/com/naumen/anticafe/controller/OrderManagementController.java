package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.orderManagment.DeleteDTO;
import com.naumen.anticafe.DTO.receive.orderManagment.RestoreDTO;
import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.helper.SearchOrderManagementHelper;
import com.naumen.anticafe.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orderManagement")
public class OrderManagementController {
    private final OrderService orderService;
    private final SearchOrderManagementHelper searchOrderManagementHelper;


    @Autowired
    public OrderManagementController(OrderService orderService, SearchOrderManagementHelper searchOrderManagementHelper) {
        this.orderService = orderService;
        this.searchOrderManagementHelper = searchOrderManagementHelper;
    }

    @GetMapping()
    public String showOrderManagement(Model model,
                                      @ModelAttribute ShowDTO dto,
                                      @AuthenticationPrincipal(expression = "username") String employeeUsername) throws NotFoundException {
        ShowSendDTO sendDTO = searchOrderManagementHelper.searchOrder(dto,true,employeeUsername);
        model.addAttribute("sendDTO",sendDTO);
        return "orderManagement";
    }

    @PostMapping("/delete")
    public String deleteOrder(@ModelAttribute DeleteDTO DTO) throws NotFoundException {
        Order order = orderService.getOrder(DTO.getOrderId());
        orderService.deleteOrderCascade(order);
        return "redirect:/orderManagement";
    }

    @PostMapping("/restore")
    public String restoreOrder(@ModelAttribute RestoreDTO DTO) throws NotFoundException {
        Order order = orderService.getOrder(DTO.getOrderId());
        order.setTaggedDelete(false);
        orderService.save(order);
        return "redirect:/orderManagement";
    }
}
