package com.naumen.anticafe.DTO.send.employee;

import com.naumen.anticafe.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
public class ShowAddEmployeeSendDTO {
    Optional<String> nameError;
    Optional<String> usernameError;
    Optional<String> passwordError;
    Optional<String> employeeName;
    List<Role> roles;
}
