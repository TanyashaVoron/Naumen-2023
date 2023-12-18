package com.naumen.anticafe.DTO.receive.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record ShowDTO(String username,Integer page) {
    public ShowDTO {
        if(page==null||page<1) page=1;
    }
}
