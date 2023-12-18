package com.naumen.anticafe.DTO.receive.employee;

import jakarta.validation.constraints.Size;

public record EmployeeDTO(@Size(min = 3, max = 20, message = "Размер имени 3-20 символов") String name,
                          @Size(min = 3, max = 20, message = "Размер имени пользователя 3-20 символов") String username,
                          @Size(min = 3, max = 20, message = "Размер Пароля 3-20 символов") String password,
                          Integer roleId) {
}
