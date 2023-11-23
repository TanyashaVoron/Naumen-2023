package com.naumen.anticafe.DTO.object;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {
    @Size(min = 3,max = 20,message = "Размер имени 3-20 символов")
    private String name;
    @Size(min = 3,max = 20,message = "Размер имени пользователя 3-20 символов")
    private String username;
    @Size(min = 3,max = 20,message = "Размер Пароля 3-20 символов")
    private String password;
    private Integer roleId;
}
