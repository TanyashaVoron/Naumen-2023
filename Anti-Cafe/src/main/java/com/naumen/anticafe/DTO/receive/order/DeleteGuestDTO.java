package com.naumen.anticafe.DTO.receive.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
@Getter
@Setter
public class DeleteGuestDTO {
    private Long orderId;
    private Long guestId;
}
