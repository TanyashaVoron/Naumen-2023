package com.naumen.anticafe.DTO.send.employee;

import com.naumen.anticafe.domain.Role;

import java.util.List;
import java.util.Optional;

public record ShowEditSendDTO(Optional<String> nameError,
                              Optional<String> usernameError,
                              Optional<String> usernameDuplicateError,
                              Optional<String> passwordError,
                              String name,
                              long employeeId,
                              String username,
                              Integer roleId,
                              String employeeName,
                              List<Role> roles) {
}
