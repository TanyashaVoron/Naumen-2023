package com.naumen.anticafe.DTO.send.order;

public record GuestCartOrderDTO(Long id,
                                String productName,
                                int quantity,
                                String guestName) {
}
