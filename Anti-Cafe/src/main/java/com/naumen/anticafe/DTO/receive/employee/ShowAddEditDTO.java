package com.naumen.anticafe.DTO.receive.employee;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ShowAddEditDTO {
    private String nameError;
    private String usernameError;
    private String usernameDuplicateError;
    private String passwordError;
    private String name;
    private Integer roleId;
    private String username;
}
