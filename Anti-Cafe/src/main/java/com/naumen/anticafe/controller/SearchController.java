package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.error.NotFoundException;
import com.naumen.anticafe.helper.SearchOrderManagementHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchOrderManagementHelper searchOrderManagementHelper;

    public SearchController(SearchOrderManagementHelper searchOrderManagementHelper) {
        this.searchOrderManagementHelper = searchOrderManagementHelper;
    }

    @GetMapping()
    @Transactional(readOnly = true)
    public String showSearch(Model model,
                             @ModelAttribute ShowDTO dto,
                             @AuthenticationPrincipal(expression = "username") String employeeUsername) throws NotFoundException {
        ShowSendDTO sendDTO = searchOrderManagementHelper.searchOrder(dto,false,employeeUsername);
        model.addAttribute("sendDTO",sendDTO);
        return "search";
    }
}
