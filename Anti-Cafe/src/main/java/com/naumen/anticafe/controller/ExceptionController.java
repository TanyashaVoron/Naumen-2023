package com.naumen.anticafe.controller;

import com.naumen.anticafe.DTO.send.error.NoAccessToOperationSendDTO;
import com.naumen.anticafe.exception.NoAccessToOperation;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.exception.ReserveException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public String notFoundException(NotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "exception";
    }

    @ExceptionHandler(NoAccessToOperation.class)
    public String noAccessToOperation(NoAccessToOperation e, Model model) {
        NoAccessToOperationSendDTO sendDTO = new NoAccessToOperationSendDTO(e.getNameEmployeeNow(), e.getOwnerOrderEmployeeName(), e.getMessage());
        model.addAttribute("sendDTO", sendDTO);
        return "noAccessToOperation";
    }

    @ExceptionHandler(ReserveException.class)
    public String reserveException(ReserveException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "exception";
    }
}
