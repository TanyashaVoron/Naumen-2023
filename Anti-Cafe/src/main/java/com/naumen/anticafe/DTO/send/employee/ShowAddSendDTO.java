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
public class ShowAddSendDTO {
    private Optional<String> nameError;
    private Optional<String> usernameError;
    private Optional<String> usernameDuplicateError;
    private Optional<String> passwordError;
    private String name;
    private String username;
    private Integer roleId;
    private String employeeName;
    private List<Role> roles;
}
