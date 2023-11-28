package com.naumen.anticafe.DTO.send.employee;

public record EmployeeSendDTO(Long id,
                              String name,
                              String username,
                              String role,
                              boolean enabled) {
}
