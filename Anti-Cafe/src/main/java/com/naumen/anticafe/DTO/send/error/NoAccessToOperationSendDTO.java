package com.naumen.anticafe.DTO.send.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NoAccessToOperationSendDTO {
    String nameEmployeeNow;
    String ownerOrderEmployeeName;
    String message;
}
