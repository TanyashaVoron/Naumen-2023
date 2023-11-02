package com.naumen.anticafe.validation;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Data
public class RegistrationValidation {
    @Size(min = 3,max = 20,message = "Размер имени 3-20 символов")
    private String name;
    @Size(min = 3,max = 20,message = "Размер имени пользователя 3-20 символов")
    private String username;
    @Size(min = 3,max = 20,message = "Размер Пароля 3-20 символов")
    private String password;
    private Long roleId;
    public Employee toEmployee(PasswordEncoder passwordEncoder, Role role){
        Employee employee = new Employee();
        employee.setName(name);
        employee.setEnabled(true);
        employee.setUsername(username);
        Set<Role> set = new HashSet<>();
        set.add(role);
        employee.setRole(set);
        employee.setPassword(passwordEncoder.encode(password));
        return employee;
    }
}
