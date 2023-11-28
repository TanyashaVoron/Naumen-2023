package com.naumen.anticafe.DTO.receive.employee;

public record ShowEditDTO(String nameError,
                          String usernameError,
                          String usernameDuplicateError,
                          String passwordError,
                          String name,
                          Integer roleId,
                          String username) {
}
