package com.naumen.anticafe.DTO.send.error;

public record NoAccessToOperationSendDTO(String nameEmployeeNow,
                                         String ownerOrderEmployeeName,
                                         String message) {

}
