package com.naumen.anticafe.DTO.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GuestCartOrderDTO {
    private Long id;
    private String productName;
    private int quantity;
    private String guestName;
}
