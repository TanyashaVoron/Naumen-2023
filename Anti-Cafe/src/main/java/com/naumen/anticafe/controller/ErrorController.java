package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.send.error.NoAccessToOperationSendDTO;
import com.naumen.anticafe.error.NoAccessToOperation;
import com.naumen.anticafe.error.NotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(NotFoundException.class)
    public String notFoundException(NotFoundException e, Model model) {
        model.addAttribute("message",e.getMessage());
        return "notFound";
    }
    @ExceptionHandler(NoAccessToOperation.class)
    public String notFoundException(NoAccessToOperation e, Model model){
        NoAccessToOperationSendDTO sendDTO = new NoAccessToOperationSendDTO(e.getNameEmployeeNow(),e.getOwnerOrderEmployeeName(),e.getMessage());
        model.addAttribute("sendDTO",sendDTO);
        return "noAccessToOperation";
    }
}
