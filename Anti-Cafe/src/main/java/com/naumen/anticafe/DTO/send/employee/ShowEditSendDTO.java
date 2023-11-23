package com.naumen.anticafe.DTO.send.employee;

import com.naumen.anticafe.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Getter
public class ShowEditSendDTO {
    private Optional<String> nameError;
    private Optional<String> usernameError;
    private Optional<String> passwordError;
    private String employeeName;
    private Long id;
    private String name;
    private String username;
    private Integer roleId;
    private List<Role> roles;
}
